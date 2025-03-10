const creationRoutes = [
    {
        path: '/creation',
        children: [
            {
                path: 'create',
                component: () => import("@/views/creation/CreationCreatePage.vue")
            }
        ],
        meta: {
            require_authentication: true,
        }
    }
]

export default creationRoutes;