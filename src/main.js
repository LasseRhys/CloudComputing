import { createApp } from "vue";

// // styles

import "@fortawesome/fontawesome-free/css/all.min.css";
import "@/assets/styles/tailwind.css";

// // mouting point for the whole app

import App from "@/App.vue";
import router from "./router";
import store from './store'


createApp(App).use(router).use(store).mount("#app");