import request from "@/request/index.js";

export const getNavigationBarUserInformation = () =>
    request({
        url: 'user/navigationBarInformation',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getAvatar = () =>
    request({
        url: 'user/avatar',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })