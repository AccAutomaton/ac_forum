const userCenterRoutes = [
    {
        path: '/userCenter',
        component: () => import("@/views/userCenter/UserCenterPage.vue"),
        children: [
            {
                path: '',
                redirect: '/userCenter/account/information',
            },
            {
                path: 'account',
                children: [
                    {
                        path: '',
                        redirect: '/userCenter/account/information',
                    },
                    {
                        path: 'information',
                        component: () => import("@/views/userCenter/account/AccountInformationTab.vue")
                    },
                    {
                        path: 'revisePassword',
                        component: () => import("@/views/userCenter/account/RevisePasswordTab.vue")
                    }
                ]
            },
            {
                path: 'purse',
                children: [
                    {
                        path: '',
                        redirect: '/userCenter/purse/balance',
                    },
                    {
                        path: 'balance',
                        component: () => import("@/views/userCenter/purse/balance/BalanceTab.vue")
                    },
                    {
                        path: 'record',
                        component: () => import("@/views/userCenter/purse/consumeRecord/ConsumeRecordTab.vue")
                    }
                ]
            }
        ],
        meta: {
            require_authentication: true,
        }
    }
];

export default userCenterRoutes;