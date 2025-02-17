const topicRoutes = [
    {
        path: '/topic',
        children: [
            {
                path: '',
                component: () => import("@/views/topic/TopicSquarePage.vue"),
            },
            {
                path: ':topicId',
                component: () => import("@/views/topic/TopicDetailPage.vue")
            }
        ],
        meta: {
            require_authentication: true,
        }
    }
]

export default topicRoutes;