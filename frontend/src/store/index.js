import {createStore} from "vuex";
import login from "@/store/login.js";
import user from "@/store/user.js";
import cos from "@/store/cos.js";

export default createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    modules: {
        login,
        user,
        cos,
    },
})