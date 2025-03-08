const articleRoutes = [
    {
        path: '/article',
        children: [
            {
                path: '',
                component: () => import("@/views/article/ArticleSqaurePage.vue"),
            },
            {
                path: ':articleId',
                component: () => import("@/views/article/ArticleDetailPage.vue")
            }
        ],
        meta: {
            require_authentication: true,
        }
    }
]

export default articleRoutes;