import {createRouter, createWebHistory} from "vue-router";

const indexRoutes = [
    {
        path: '/',
        name: 'index',
        redirect: '/home',
    },
    {
        path: '/home',
        name: 'home',
        component: () => import('@/views/Home.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: indexRoutes,
})

export default router