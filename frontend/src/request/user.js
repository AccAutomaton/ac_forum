import request from "@/request/index.js";

export const getNavigationBarUserInformation = () =>
    request({
        url: 'user/get/navigationBarInformation',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getAvatarGetAuthorization = () =>
    request({
        url: '/user/get/avatar/getAuthorization',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getAvatarUpdateAuthorization = () =>
    request({
        url: 'user/get/avatar/updateAuthorization',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const setAvatarCustomization = () =>
    request({
        url: 'user/set/avatar/customization',
        method: 'PATCH',
    }).then((response) => {
        return response["data"];
    })

export const getUserDetails = () =>
    request({
        url: 'user/get/details',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const setNickname = (newNickname) =>
    request({
        url: 'user/set/nickname',
        method: 'PATCH',
        data: {
            newNickname: newNickname
        },
    }).then((response) => {
        return response["data"];
    })