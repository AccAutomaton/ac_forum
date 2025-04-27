const creationRoutes = [
    {
        path: '/creation',
        redirect: '/creation/dashboard',
        component: () => import('@/views/creation/center/CreationCenterPage.vue'),
        children: [
            {
                path: 'dashboard',
                component: () => import("@/views/creation/center/dashboard/DashboardTab.vue")
            },
            {
                path: 'topics',
                component: () => import("@/views/creation/center/TopicsTab.vue")
            },
            {
                path: 'articles',
                component: () => import("@/views/creation/center/ArticlesTab.vue")
            }
        ],
        meta: {
            require_authentication: true,
        }
    },
    {
        path: '/creation/create',
        component: () => import("@/views/creation/CreationCreatePage.vue")
    },
    {
        path: '/creation/edit/:articleId',
        component: () => import("@/views/creation/CreationEditPage.vue")
    },
]

export default creationRoutes;