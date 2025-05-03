const consoleRoutes = [
    {
        path: '/console',
        component: () => import('@/views/console/ConsolePage.vue'),
        children: [
            {
                path: '/console/notification',
                component: () => import('@/views/console/NotificationConsole.vue')
            },
            {
                path: '/console/elasticsearch',
                component: () => import('@/views/console/ElasticSearchConsole.vue')
            },
            {
                path: '/console/sql',
                component: () => import('@/views/console/SqlConsole.vue')
            },
            {
                path: '/console/user',
                component: () => import('@/views/console/UserConsole.vue')
            },
            {
                path: '/console/online',
                component: () => import('@/views/console/OnlineConsole.vue')
            }
        ],
        meta: {
            require_authentication: true,
            require_user_type_lower_than: 3,
        }
    }
];

export default consoleRoutes;