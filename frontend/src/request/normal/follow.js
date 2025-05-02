import request from "@/request/normal/index.js";

export const follow = (targetUid) => request({
    url: '/follow',
    method: 'PUT',
    params: {
        targetUid: targetUid,
    }
}).then(response => response["data"])

export const unfollow = (targetUid) => request({
    url: '/follow',
    method: 'DELETE',
    params: {
        targetUid: targetUid,
    }
}).then(response => response["data"])

export const hadFollowed = (targetUid) => request({
    url: `/follow/${targetUid}`,
    method: 'GET',
}).then(response => response["data"])

export const getFansIncreamentNearly7Days = () =>
    request({
        url: '/follow/increament',
        method: 'GET',
    }).then(response => response["data"])