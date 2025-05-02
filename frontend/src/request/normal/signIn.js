import request from "@/request/normal/index.js";

const date = new Date();

export const getSignInInfo = (year = date.getFullYear(), month = date.getMonth() + 1) =>
    request({
        url: '/signIn/info',
        method: 'GET',
        params: {
            year: year,
            month: month,
        }
    }).then(response => response["data"])

export const signIn = () =>
    request({
        url: '/signIn',
        method: 'PUT',
    }).then(response => response["data"])