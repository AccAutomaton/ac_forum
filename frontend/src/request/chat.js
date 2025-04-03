import request from "@/request/index.js";

export const createChat = (targetUid) =>
    request({
        url: '/chat',
        method: 'PUT',
        params: {
            receiver: targetUid,
        }
    }).then(response => {
        return response["data"];
    })

export const getChatList = (pageNumber, pageSize) =>
    request({
        url: '/chat/list',
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then(response => {
        return response["data"];
    })

export const getChatById = (id) =>
    request({
        url: `/chat/${id}`,
        method: 'GET',
    }).then(response => {
        return response["data"];
    })

export const readChatById = (id) =>
    request({
        url: `/chat/${id}/read`,
        method: 'PATCH',
    }).then(response => {
        return response["data"];
    })

export const getChatMessageByChatId = (chatId, pageSize, before = null) =>
    request({
        url: `/chat/${chatId}/message/list`,
        method: 'GET',
        params: {
            pageSize: pageSize,
            before: before,
        }
    }).then(response => {
        return response["data"];
    })

export const sendChatMessage = (chatId, message) =>
    request({
        url: `/chat/${chatId}/message`,
        method: 'PUT',
        data: {
            content: message,
        }
    }).then(response => {
        return response["data"];
    })

export const getImageUploadAuthorizationByChatId = (chatId) =>
    request({
        url: `/chat/${chatId}/image/authorization/upload`,
        method: 'GET',
    }).then(response => {
        return response["data"];
    })

export const getImageDownloadAuthorizationByChatId = (chatId) =>
    request({
        url: `/chat/${chatId}/image/authorization/download`,
        method: 'GET',
    }).then(response => {
        return response["data"];
    })

export const sendChatImageMessage = (chatId, imageFileName) =>
    request({
        url: `/chat/${chatId}/message/image`,
        method: 'PUT',
        params: {
            imageFileName: imageFileName,
        }
    }).then(response => {
        return response["data"];
    })