# LuminaSearch

LuminaSearch is a simple, lightweight search engine implementation demonstrating a full-stack application with a Java backend and a vanilla JavaScript frontend. It serves a mock database of search results through a dedicated HTTP server.

## Features

- **Search Functionality**: Instant search results from a pre-defined mock database.
- **REST API**: Custom-built REST endpoint `/api/search` handling query parameters.
- **Static File Serving**: Serves HTML, CSS, and JS files directly.
- **Modern UI**: Clean, responsive interface with a glassmorphism design style.
- **No External Dependencies**: Built using only standard Java libraries (`com.sun.net.httpserver`) and vanilla web technologies.

## Project Structure

```
LuminaSearch/
├── src/
│   └── SearchServer.java    # Main Java server and logic
├── public/                 # Frontend assets
│   ├── index.html
│   ├── style.css
│   └── script.js
└── README.md
```

## Setup & Configuration

### Prerequisites
- Java Development Kit (JDK) 8 or higher.

### Important Configuration
> [!IMPORTANT]
> **Hardcoded Path**: The server uses a hardcoded absolute path to serve the `public` directory.
> You **MUST** update the `PUBLIC_DIR` constant in `src/SearchServer.java` if you move the project.
>
> **File:** `src/SearchServer.java`
> ```java
> private static final String PUBLIC_DIR = "d:/project/LuminaSearch/public";
> ```
> Change this path to match the location of the `public` folder on your machine.

## How to Run

1.  **Compile the Java Server**:
    Open a terminal in the project root and run:
    ```bash
    javac src/SearchServer.java -d bin
    ```
    *(Ensure the `bin` directory exists or create it: `mkdir bin`)*

2.  **Start the Server**:
    ```bash
    java -cp bin SearchServer
    ```

3.  **Access the Application**:
    Open your web browser and navigate to:
    `http://localhost:8000`

## API Usage

**Endpoint:** `GET /api/search?q=<query>`

**Example:**
`http://localhost:8000/api/search?q=java`

**Response:**
```json
[
  {
    "title": "Java Programming Language",
    "url": "https://www.java.com",
    "description": "Java is a high-level..."
  }
]
```
