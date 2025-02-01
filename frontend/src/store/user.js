const userInformation = {
    state: () => ({
        uid: '',
        username: '',
        nickname: '',
        avatar: '',
        userType: {
            index: 3,
            value: '普通用户'
        }
    }),
    getters: {
        getUid: (state) => state.uid,
        getUsername: (state) => state.username,
        getNickname: (state) => state.nickname,
        getAvatar: (state) => state.avatar,
        getUserType: (state) => state.userType,
    },
    mutations: {
        clearUserInformation: (state) => {
            state.uid = '';
            state.username = '';
            state.nickname = '';
            state.avatar = '';
            state.userType = {
                index: 3,
                value: '普通用户',
            }
        },
        setUid: (state, uid) => {
            state.uid = uid;
        },
        setUsername: (state, username) => {
            state.username = username;
        },
        setNickname: (state, nickname) => {
            state.nickname = nickname;
        },
        setAvatar: (state, avatar) => {
            state.avatar = avatar;
        },
        setUserType: (state, type) => {
            state.userType = type;
        },
    },
    actions: {},
    modules: {},
}

export default userInformation;