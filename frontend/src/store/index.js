import {createStore} from "vuex";
import login from "@/store/login.js";
import user from "@/store/user.js";

export default createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    modules: {
        login,
        user,
    },
})