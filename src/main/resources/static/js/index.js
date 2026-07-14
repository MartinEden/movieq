import { createApp, ref } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js'

window.startVue = function(movies, allTags) {
    const sortFunction = function (sortMode) {
        if (sortMode === "dateAdded") {
            return (a, b) => a.dateAdded.localeCompare(b.dateAdded);
        } else if (sortMode === "rating") {
            return (a, b) => (a.rating ?? 0) - (b.rating ?? 0);
        } else if (sortMode === "tomato") {
            return (a, b) => (a.tomatoMeter ?? 0) - (b.tomatoMeter ?? 0);
        } else {
            throw new Error("Unknown sortMode " + sortMode);
        }
    };

    const tagFilter = function (movie, tag) {
        const {name, status} = tag;
        return status === 0
            || (status === 1 && movie.tags.includes(name))
            || (status === -1 && !movie.tags.includes(name));
    };

    const sortModes = [
        {id: "dateAdded", text: "Date added"},
        {id: "rating", text: "Movie DB rating"},
        {id: "tomato", text: "Tomatometer"},
    ];

    createApp({
        data() {
            return {
                movies: movies,
                sortMode: "dateAdded",
                sortModes: sortModes,
                sortReversed: true,
                tags: Object.fromEntries(allTags.map(t => [t, 0])),
            }
        },
        methods: {
            setSortMode(newSortMode) {
                if (this.sortMode !== newSortMode) {
                    this.sortMode = newSortMode;
                    this.sortReversed = false;
                } else {
                    this.sortReversed = !this.sortReversed;
                }
            },
            toggleTag(tag) {
                let x = this.tags[tag];
                x++;
                if (x > 1) {
                    x = -1;
                }
                this.tags[tag] = x;
            },
            clearFilters() {
                for (const tag in this.tags) {
                    this.tags[tag] = 0;
                }
            },
            watchLink(movie) {
                return "https://www.justwatch.com/uk/search?q=" + movie.title
            },
            async markWatched(movie) {
                movie.dateWatched = new Date().toISOString();
                try {
                    const response = await fetch(`./movie/${movie.imdbId}/mark-watched/`, {method: 'post'});
                    if (!response.ok) {
                        throw new Error(`Response status: ${response.status}`);
                    }
                    // …
                } catch (error) {
                    console.log(error)
                    alert(error.message);
                }
            }
        },
        computed: {
            filteredMovies() {
                const tags = Object.entries(this.tags).map(e => ({name: e[0], status: e[1]}));
                const filtered = this.movies
                    .filter(m => tags.every(t => tagFilter(m, t)))
                    .filter(m => m.dateWatched === null);

                const sorted = filtered.toSorted(sortFunction(this.sortMode));
                if (this.sortReversed) {
                    sorted.reverse();
                }
                return sorted;
            }
        }
    }).mount('#vueApp')
}