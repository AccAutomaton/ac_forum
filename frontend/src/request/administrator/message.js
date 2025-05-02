import request from "@/request/normal/index.js";

export const sendTemporaryNormalMessage = (uid, content, targetUrl = "", title = "系统通知") =>
    request({
        url: '/administrator/message/send/temporary',
        method: 'PUT',
        data: {
            uid: uid,
            title: title,
            type: 0,
            content: content,
            targetUrl: targetUrl,
        }
    }).then(response => {
        return response["data"];
    })

export const sendPerpetualNormalMessage = (uid, content, targetUrl = "", title = "系统通知") =>
    request({
        url: '/administrator/message/send/perpetual',
        method: 'PUT',
        data: {
            uid: uid,
            title: title,
            type: 0,
            content: content,
            targetUrl: targetUrl,
        }
    }).then(response => {
        return response["data"];
    })