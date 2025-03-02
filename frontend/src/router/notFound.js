const notFoundRoutes = [
    {
        path: "/404",
        name: "404",
        component: () => import("@/views/NotFoundPage.vue"),
        meta: {
            requestAuth: false,
        }
    },
    {
        path: "/:catchAll(.*)",
        redirect: "/404",
    }
]

export default notFoundRoutes;