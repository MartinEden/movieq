import { createApp, ref } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js'

window.startVue = function(movies, allTags) {
    var sortFunction = function(sortMode) {
        if (sortMode == "dateAdded") {
            return (a, b) => a.dateAdded.localeCompare(b.dateAdded);
        } else if (sortMode == "rating") {
            return (a, b) => a.rating - b.rating;
        } else if (sortMode == "tomato") {
            return (a, b) => a.tomatoMeter - b.tomatoMeter;
        } else {
            throw Exception("Unknown sortMode " + sortMode);
        }
    };

    var tagFilter = function(movie, tag) {
        var { name, status } = tag;
        return status == 0
            || (status == 1 && movie.tags.includes(name))
            || (status == -1 && !movie.tags.includes(name));
    }

    var sortModes = [
        { id: "dateAdded", text: "Date added" },
        { id: "rating", text: "IMDB rating" },
        { id: "tomato", text: "Tomatometer" },
    ];

    createApp({
        data() {
            return {
                movies: movies,
                sortMode: "dateAdded",
                sortModes: sortModes,
                sortReversed: false,
                tags: Object.fromEntries(allTags.map(t => [t, 0])),
            }
        },
        methods: {
            setSortMode(newSortMode) {
                if (this.sortMode != newSortMode) {
                    this.sortMode = newSortMode;
                    this.sortReversed = false;
                } else {
                    this.sortReversed = !this.sortReversed;
                }
            },
            toggleTag(tag) {
                var x = this.tags[tag];
                x++;
                if (x > 1) {
                    x = -1;
                }
                this.tags[tag] = x;
            }
        },
        computed: {
            filteredMovies() {
                var tags = Object.entries(this.tags).map(e => ({ name: e[0], status: e[1] }));
                var filtered = this.movies.filter(m => tags.every(t => tagFilter(m, t)));

                var sorted = filtered.toSorted(sortFunction(this.sortMode));
                if (this.sortReversed) {
                    sorted.reverse();
                }
                return sorted;
            }
        }
    }).mount('#vueApp')
}