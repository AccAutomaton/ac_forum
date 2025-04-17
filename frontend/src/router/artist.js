const artistRoutes = [
    {
        path: '/artist/:artistId',
        children: [
            {
                path: 'livingRoom',
                component: () => import("@/views/artist/LivingRoomPage.vue"),
            }
        ],
        meta: {
            require_authentication: true,
        }
    }
]

export default artistRoutes;