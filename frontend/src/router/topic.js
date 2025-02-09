const topicRoutes = [
    {
        path: '/topic',
        component: () => import("@/views/topic/TopicSquarePage.vue"),
        meta: {
            require_authentication: true,
        }
    }
]

export default topicRoutes;