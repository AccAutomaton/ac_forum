import request from "@/request/index.js";

export const getNavigationBarUserInformation = () =>
    request({
        url: '/user/navigationBarInformation',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getAvatarGetAuthorization = () =>
    request({
        url: '/user/avatar',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getAvatarUpdateAuthorization = () =>
    request({
        url: '/user/avatar/updateAuthorization',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const setAvatarCustomization = () =>
    request({
        url: '/user/avatar/customization',
        method: 'PATCH',
    }).then((response) => {
        return response["data"];
    })

export const getUserDetails = () =>
    request({
        url: '/user/details',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const setNickname = (newNickname) =>
    request({
        url: '/user/nickname',
        method: 'PATCH',
        data: {
            newNickname: newNickname
        },
    }).then((response) => {
        return response["data"];
    })

export const getEmailVerifyCodeForSettingEmail = (newEmail, captchaUUID, captchaCode) =>
    request({
        url: '/user/emailVerifyCode/setEmail',
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
        url: '/user/email',
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
        url: '/user/password',
        method: 'PATCH',
        data: {
            oldPassword: oldPassword,
            newPassword: newPassword,
        }
    }).then((response) => {
        return response["data"];
    })