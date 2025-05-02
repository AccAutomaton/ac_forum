import {createRouter, createWebHistory} from "vue-router";
import store from "@/store/index.js";
import {GetAuthorizationCode} from "@/request/index.js";
import userCenterRoutes from "@/router/userCenter.js";
import topicRoutes from "@/router/topic.js";
import vipRoutes from "@/router/vip.js";
import notFoundRoutes from "@/router/notFound.js";
import articleRoutes from "@/router/article.js";
import creationRoutes from "@/router/creation.js";
import chatRoutes from "@/router/chat.js";
import artistRoutes from "@/router/artist.js";

const indexRoutes = [
    {
        path: '/',
        redirect: '/home',
    },
    {
        path: '/login',
        component: () => import('@/views/login/LoginPage.vue'),
    },
    {
        path: '/findBackPassword',
        component: () => import('@/views/login/FindBackPasswordPage.vue')
    },
    {
        path: '/home',
        component: () => import('@/views/home/HomePage.vue'),
        meta: {
            require_authentication: true,
        }
    }
];

// noinspection JSCheckFunctionSignatures
const routes = indexRoutes.concat(userCenterRoutes, topicRoutes, vipRoutes, articleRoutes, creationRoutes, chatRoutes, artistRoutes, notFoundRoutes)

const router = createRouter({
    history: createWebHistory(),
    routes: routes,
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
    } else {
        next();
    }
})

export default router