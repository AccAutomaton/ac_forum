import request from "@/request/normal/index.js";

export const getOnlineUserCount = () =>
    request({
        url: '/administrator/user/online',
        method: 'GET',
    }).then(response => response["data"]);