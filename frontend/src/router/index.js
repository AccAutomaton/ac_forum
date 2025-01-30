import {createRouter, createWebHistory} from "vue-router";
import store from "@/store/index.js";
import {GetAuthorizationCode} from "@/request/index.js";

const indexRoutes = [
    {
        path: '/',
        name: 'index',
        redirect: '/home',
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/login/LoginPage.vue'),
        meta: {
            require_no_authentication: true,
        }
    },
    {
        path: '/findBackPassword',
        name: 'findBackPassword',
        component: () => import('@/views/login/FindBackPasswordPage.vue')
    },
    {
        path: '/home',
        name: 'home',
        component: () => import('@/views/HomePage.vue'),
        meta: {
            require_authentication: true,
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: indexRoutes,
})

router.beforeEach((to, from, next) => {
    if (to.meta["require_authentication"]) {
        if (store.getters.getIsLogin || GetAuthorizationCode()) {
            next();
        } else {
            next({
                path: '/login',
            })
        }
    } else if (to.meta["require_no_authentication"]) {
        if (store.getters.getIsLogin || GetAuthorizationCode()) {
            next({
                path: from.path,
            })
        } else {
            next();
        }
    }
    else {
        next();
    }
})

export default router