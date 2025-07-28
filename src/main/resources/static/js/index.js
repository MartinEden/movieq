import { createApp, ref } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js'

window.startVue = function(movies) {
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
                sortReversed: false
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
            }
        },
        computed: {
            filteredMovies() {
                var sorted = this.movies.toSorted(sortFunction(this.sortMode));
                if (this.sortReversed) {
                    sorted.reverse();
                }
                return sorted;
            }
        }
    }).mount('#vueApp')
}