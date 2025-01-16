import { createStore } from 'vuex';
import auth from './modules/auth';

const store = createStore({
    modules: {
        auth
    },
    state: {},
    mutations: {},
    actions: {},
    getters: {}
});

export default store;