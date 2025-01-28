const loginInformation = {
    state: {
        isLogin: false,
        authorizationCode: '',
    },
    getters: {
        getIsLogin: (state) => state.isLogin,
        getAuthorizationCode: (state) => state.authorizationCode,
    },
    mutations: {
        clearLoginInformation: (state) => {
            state.isLogin = false;
            state.authorizationCode = '';
        },
        setIsLogin: (state, isLogin) => {
            state.isLogin = isLogin;
        },
        setAuthorizationCode: (state, authorizationCode)=> {
            state.authorizationCode = authorizationCode;
        }
    },
    actions: {},
    modules: {},
}

export default loginInformation;