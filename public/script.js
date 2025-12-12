document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');
    const resultsContainer = document.getElementById('resultsContainer');
    const loading = document.getElementById('loading');

    // Auto-focus input
    searchInput.focus();

    searchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const query = searchInput.value.trim();
        
        if (!query) return;

        // UI State: Loading
        document.body.classList.add('has-results');
        resultsContainer.innerHTML = '';
        resultsContainer.classList.add('hidden');
        loading.classList.remove('hidden');

        try {
            // Simulate network delay for "premium" feel (optional, but nice)
            await new Promise(resolve => setTimeout(resolve, 800));

            const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`);
            if (!response.ok) throw new Error('Network response was not ok');
            
            const results = await response.json();
            
            displayResults(results);
        } catch (error) {
            console.error('Error fetching search results:', error);
            resultsContainer.innerHTML = `<div class="result-card" style="text-align: center; color: #ef4444;">
                <p>Something went wrong. Please try again later.</p>
            </div>`;
            resultsContainer.classList.remove('hidden');
        } finally {
            loading.classList.add('hidden');
        }
    });

    function displayResults(results) {
        if (results.length === 0) {
            resultsContainer.innerHTML = `<div class="result-card" style="text-align: center;">
                <p>No results found for "<strong>${searchInput.value}</strong>"</p>
            </div>`;
        } else {
            resultsContainer.innerHTML = results.map(result => `
                <div class="result-card">
                    <a href="${result.url}" class="result-title" target="_blank">${result.title}</a>
                    <a href="${result.url}" class="result-url" target="_blank">${result.url}</a>
                    <p class="result-desc">${result.description}</p>
                </div>
            `).join('');
        }
        
        // Staggered animation for results
        resultsContainer.classList.remove('hidden');
        const cards = resultsContainer.querySelectorAll('.result-card');
        cards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(10px)';
            card.style.transition = `all 0.3s ease ${index * 0.1}s`;
            
            // Trigger reflow
            card.offsetHeight;
            
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        });
    }
});
