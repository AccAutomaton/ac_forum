const chatRoutes = [
    {
        path: '/chat',
        children: [
            {
                path: '',
                component: () => import("@/views/chat/ChatPage.vue"),
            },
            {
                path: ':id',
                component: () => import("@/views/chat/ChatPage.vue")
            }
        ],
        meta: {
            require_authentication: true,
        }
    }
]

export default chatRoutes;