import request from "@/request/index.js";

export const getNavigationBarUserInformation = () =>
    request({
        url: 'user/navigationBarInformation',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })