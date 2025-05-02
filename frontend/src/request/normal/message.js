import request from "@/request/normal/index.js";

export const getNotSeenMessageCount = () =>
    request({
        url: '/message/count/notSeen',
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getMessageList = (pageNumber, pageSize, seen) =>
    request({
        url: '/message/list',
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            seen: seen,
        }
    }).then((response) => {
        return response["data"];
    })

export const doReadMessage = (messageId) =>
    request({
        url: '/message/read',
        method: 'PATCH',
        params: {
            messageId: messageId,
        }
    }).then((response) => {
        return response["data"];
    })