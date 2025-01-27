import request from "@/request/index.js";

export const getGraphicCaptchaImage = () =>
    request({
        url: '/getCaptcha',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getEmailVerifyCodeForRegister = (email, captchaUUID, captchaCode) =>
    request({
        url: '/getEmailVerifyCode/register',
        method: 'POST',
        data: {
            email: email,
            captchaUUID: captchaUUID,
            captchaCode: captchaCode,
        }
    }).then((response) => {
        console.log(response);
        return response["data"];
    })


export const register = (username, password, email, verifycode) =>
    request({
        url: '/register',
        method: 'POST',
        data: {
            username: username,
            password: password,
            email: email,
            verifycode: verifycode,
        }
    }).then((response) => {
        return response["data"];
    })

export const login = (username, password) =>
    request({
        url: '/login',
        method: 'POST',
        data: {
            username: username,
            password: password,
        }
    }).then((response) => {
        return response["data"];
    })