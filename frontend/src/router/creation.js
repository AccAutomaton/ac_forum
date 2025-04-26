const creationRoutes = [
    {
        path: '/creation',
        redirect: '/creation/dashboard',
        component: () => import('@/views/creation/center/CreationCenterPage.vue'),
        children: [
            {
                path: 'create',
                component: () => import("@/views/creation/CreationCreatePage.vue")
            },
            {
                path: 'edit/:articleId',
                component: () => import("@/views/creation/CreationEditPage.vue")
            },
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
    }
]

export default creationRoutes;