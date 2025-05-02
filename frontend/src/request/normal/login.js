import request from "@/request/normal/index.js";

export const getGraphicCaptchaImage = () =>
    request({
        url: '/captcha',
        method: 'GET',
    }).then(response => response["data"])

export const getEmailVerifyCodeForRegister = (email, captchaUUID, captchaCode) =>
    request({
        url: '/emailVerifyCode/register',
        method: 'POST',
        data: {
            email: email,
            captchaUUID: captchaUUID,
            captchaCode: captchaCode,
        }
    }).then(response => response["data"])


export const register = (username, password, email, verifycode) =>
    request({
        url: '/register',
        method: 'POST',
        data: {
            username: username,
            password: password,
            email: email,
            verifyCode: verifycode,
        }
    }).then(response => response["data"])

export const login = (username, password) =>
    request({
        url: '/login',
        method: 'POST',
        data: {
            username: username,
            password: password,
        }
    }).then(response => response["data"])

export const getEmailVerifyCodeForFindingBackPassword = (username, email, captchaUUID, captchaCode) =>
    request({
        url: '/emailVerifyCode/findBackPassword',
        method: 'POST',
        data: {
            username: username,
            email: email,
            captchaUUID: captchaUUID,
            captchaCode: captchaCode,
        }
    }).then(response => response["data"])

export const findBackPassword = (username, verifyCode, newPassword) =>
    request({
        url: '/findBackPassword',
        method: 'PATCH',
        data: {
            username: username,
            verifyCode: verifyCode,
            newPassword: newPassword,
        }
    }).then(response => response["data"])