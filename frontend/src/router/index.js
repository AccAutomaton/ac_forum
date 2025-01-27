import {createRouter, createWebHistory} from "vue-router";

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
    },
    {
        path: '/home',
        name: 'home',
        component: () => import('@/views/HomePage.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: indexRoutes,
})

export default router