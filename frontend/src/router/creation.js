const creationRoutes = [
    {
        path: '/creation',
        children: [
            {
                path: 'create',
                component: () => import("@/views/creation/CreationCreatePage.vue")
            },
            {
                path: 'edit/:articleId',
                component: () => import("@/views/creation/CreationEditPage.vue")
            }
        ],
        meta: {
            require_authentication: true,
        }
    }
]

export default creationRoutes;