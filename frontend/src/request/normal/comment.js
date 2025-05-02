import request from "@/request/normal/index.js";

export const getCommentById = (commentId) =>
    request({
        url: '/comment/' + commentId,
        method: 'GET',
    }).then(response => {
        return response["data"];
    })

export const thumbsUpCommentById = (commentId) =>
    request({
        url: '/comment/' + commentId + '/thumbsUp',
        method: 'PUT',
    }).then(response => {
        return response["data"];
    })

export const unThumbsUpCommentById = (commentId) =>
    request({
        url: '/comment/' + commentId + '/thumbsUp',
        method: 'DELETE',
    }).then(response => {
        return response["data"];
    })

export const deleteCommentById = (commentId) =>
    request({
        url: '/comment/' + commentId,
        method: 'DELETE',
    }).then(response => {
        return response["data"];
    })