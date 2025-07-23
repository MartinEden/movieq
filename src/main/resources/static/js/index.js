import { createApp, ref } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js'

window.startVue = function(movies) {
    createApp({
        data() {
            return {
                movies: movies
            }
        }
    }).mount('#vueApp')
}