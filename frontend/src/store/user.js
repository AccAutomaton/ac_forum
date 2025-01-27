const userInformation = {
    state: () => ({
        uid: '',
        username: '',
        userType: {
            index: 3,
            value: '普通用户'
        }
    }),
    getters: {
        getUid: (state) => state.uid,
        getUsername: (state) => state.username,
        getUserType: (state) => state.userType,
    },
    mutations: {
        setUid: (state, uid) => {
            state.uid = uid;
        },
        setUsername: (state, username) => {
            state.username = username;
        },
        setUserType: (state, type) => {
            state.userType = type;
        },
    },
    actions: {},
    modules: {},
}

export default userInformation;