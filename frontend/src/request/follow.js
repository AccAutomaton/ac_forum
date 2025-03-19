import request from "@/request/index.js";

export const follow = (targetUid) => request({
    url: 'follow/do',
    method: 'PATCH',
    params: {
        targetUid: targetUid,
    }
}).then(response => {
    return response["data"];
})

export const unfollow = (targetUid) => request({
    url: 'follow/undo',
    method: 'PATCH',
    params: {
        targetUid: targetUid,
    }
}).then(response => {
    return response["data"];
})