import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SearchServer {

    private static final int PORT = 8000;
    private static final String PUBLIC_DIR = "d:/AntiGravity/LuminaSearch/public";

    // Mock Data
    private static class SearchResult {
        String title;
        String url;
        String description;

        SearchResult(String title, String url, String description) {
            this.title = title;
            this.url = url;
            this.description = description;
        }
    }

    private static final List<SearchResult> database = new ArrayList<>();

    static {
        database.add(new SearchResult("Java Programming Language", "https://www.java.com", "Java is a high-level, class-based, object-oriented programming language."));
        database.add(new SearchResult("MDN Web Docs", "https://developer.mozilla.org", "Resources for developers, by developers. Documenting web technologies, including CSS, HTML, and JavaScript."));
        database.add(new SearchResult("Stack Overflow", "https://stackoverflow.com", "A public platform building the definitive collection of coding questions and answers."));
        database.add(new SearchResult("GitHub", "https://github.com", "GitHub is where over 100 million developers shape the future of software, together."));
        database.add(new SearchResult("Google", "https://www.google.com", "Search the world's information, including webpages, images, videos and more."));
        database.add(new SearchResult("W3Schools", "https://www.w3schools.com", "W3Schools is optimized for learning and training. Examples might be simplified to improve reading and learning."));
        database.add(new SearchResult("GeeksforGeeks", "https://www.geeksforgeeks.org", "A Computer Science portal for geeks. It contains well written, well thought and well explained computer science and programming articles."));
        database.add(new SearchResult("React", "https://react.dev", "The library for web and native user interfaces."));
        database.add(new SearchResult("Spring Framework", "https://spring.io", "Spring makes Java simple."));
        database.add(new SearchResult("Hibernate ORM", "https://hibernate.org", "Hibernate ORM is the de facto standard Object/Relational Mapping (ORM) library for backend Java applications."));
        database.add(new SearchResult("YouTube", "https://www.youtube.com", "Enjoy the videos and music you love, upload original content, and share it all with friends, family, and the world on YouTube."));
        database.add(new SearchResult("Wikipedia", "https://www.wikipedia.org", "Wikipedia is a free online encyclopedia, created and edited by volunteers around the world and hosted by the Wikimedia Foundation."));
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API Context
        server.createContext("/api/search", new SearchHandler());

        // Static File Context (Default)
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // creates a default executor
        System.out.println("LuminaSearch Server started on port " + PORT);
        server.start();
    }

    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("GET".equals(t.getRequestMethod())) {
                String query = t.getRequestURI().getQuery();
                String searchTerm = "";
                if (query != null && query.contains("q=")) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        if (param.startsWith("q=")) {
                             searchTerm = URLDecoder.decode(param.substring(2), StandardCharsets.UTF_8).toLowerCase();
                             break;
                        }
                    }
                }

                String finalSearchTerm = searchTerm;
                List<SearchResult> results = database.stream()
                        .filter(r -> r.title.toLowerCase().contains(finalSearchTerm) || r.description.toLowerCase().contains(finalSearchTerm))
                        .collect(Collectors.toList());

                // Manual JSON construction to avoid external deps
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < results.size(); i++) {
                    SearchResult r = results.get(i);
                    json.append(String.format("{\"title\": \"%s\", \"url\": \"%s\", \"description\": \"%s\"}",
                            escapeJson(r.title), escapeJson(r.url), escapeJson(r.description)));
                    if (i < results.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // For development
                t.sendResponseHeaders(200, response.length);
                OutputStream os = t.getResponseBody();
                os.write(response);
                os.close();
            } else {
                 t.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
        
        private String escapeJson(String s) {
            return s.replace("\"", "\\\""); 
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File(PUBLIC_DIR + path);
            if (file.exists() && !file.isDirectory()) {
                String contentType = "text/plain";
                if (path.endsWith(".html")) contentType = "text/html";
                else if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                else if (path.endsWith(".png")) contentType = "image/png";
                else if (path.endsWith(".jpg")) contentType = "image/jpeg";
                
                t.getResponseHeaders().set("Content-Type", contentType);
                t.sendResponseHeaders(200, file.length());
                OutputStream os = t.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            } else {
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
