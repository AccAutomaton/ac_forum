import request from "@/request/index.js";

export const getNavigationBarUserInformation = () =>
    request({
        url: '/user/get/navigationBarInformation',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getAvatarGetAuthorization = () =>
    request({
        url: '/user/get/avatar',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getAvatarUpdateAuthorization = () =>
    request({
        url: '/user/get/avatar/updateAuthorization',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const setAvatarCustomization = () =>
    request({
        url: '/user/set/avatar/customization',
        method: 'PATCH',
    }).then((response) => {
        return response["data"];
    })

export const getUserDetails = () =>
    request({
        url: '/user/get/details',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const setNickname = (newNickname) =>
    request({
        url: '/user/set/nickname',
        method: 'PATCH',
        data: {
            newNickname: newNickname
        },
    }).then((response) => {
        return response["data"];
    })

export const getEmailVerifyCodeForSettingEmail = (newEmail, captchaUUID, captchaCode) =>
    request({
        url: '/user/getEmailVerifyCode/setEmail',
        method: 'POST',
        data: {
            newEmail: newEmail,
            captchaUUID: captchaUUID,
            captchaCode: captchaCode,
        }
    }).then((response) => {
        return response["data"];
    })

export const setEmail = (newEmail, verifyCode) =>
    request({
        url: '/user/set/email',
        method: 'PATCH',
        data: {
            newEmail: newEmail,
            verifyCode: verifyCode,
        }
    }).then((response) => {
        return response["data"];
    })

export const setPassword = (oldPassword, newPassword) =>
    request({
        url: '/user/set/password',
        method: 'PATCH',
        data: {
            oldPassword: oldPassword,
            newPassword: newPassword,
        }
    }).then((response) => {
        return response["data"];
    })

export const getCoins = () => request({
    url: '/user/get/balance/coin',
    method: 'GET',
}).then((response) => {
    return response["data"];
})

export const buyCoins = (coins) =>
    request({
        url: 'user/buy/coin',
        method: 'POST',
        params: {
            coins: coins,
        }
    }).then(response => {
        return response["data"];
    })

export const afterPaying = (tradeId) =>
    request({
        url: 'user/buy/coin/payed',
        method: 'POST',
        params: {
            tradeId: tradeId,
        }
    }).then(response => {
        return response["data"];
    })

export const refreshPayingStatus = () =>
    request({
        url: 'user/pay/coin/refresh',
        method: 'POST',
    }).then(response => {
        return response["data"];
    })