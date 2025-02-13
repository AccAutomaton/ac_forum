const vipRoutes = [
    {
        path: '/vip',
        component: () => import("@/views/vip/VipPage.vue"),
        meta: {
            require_authentication: true,
        }
    }
]

export default vipRoutes;