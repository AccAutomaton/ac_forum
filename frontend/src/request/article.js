import request from "@/request/index.js";

export const queryArticleListOfTopic = (topicId, pageNumber, pageSize, queryType, keyword = "") =>
    request({
        url: '/article/list',
        method: 'GET',
        params: {
            topicId: topicId,
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
            keyword: keyword,
        }
    }).then(response => {
        return response["data"];
    })

export const queryArticleList = (pageNumber, pageSize, queryType, keyword = "") =>
    request({
        url: '/article/list',
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
            keyword: keyword,
        }
    }).then(response => {
        return response["data"];
    })

export const getArticleImageUploadAuthorization = () =>
    request({
        url: '/article/image/updateAuthorization',
        method: 'GET',
    }).then(response => {
        return response["data"];
    })

export const createArticle = (topic, title, content) =>
    request({
        url: '/article/create',
        method: 'PUT',
        data: {
            topic: topic,
            title: title,
            content: content,
        }
    }).then(response => {
        return response["data"];
    })

export const getArticleById = (id) =>
    request({
        url: '/article/' + id,
        method: 'GET',
    }).then(response => {
        return response["data"];
    })

export const thumbsUpArticle = (id) =>
    request({
        url: '/article/' + id + '/thumbsUp',
        method: 'PATCH',
    }).then(response => {
        return response["data"];
    })

export const unThumbsUpArticle = (id) =>
    request({
        url: '/article/' + id + '/unThumbsUp',
        method: 'PATCH',
    }).then(response => {
        return response["data"];
    })

export const collectArticle = (id) =>
    request({
        url: '/article/' + id + '/collect',
        method: 'PATCH',
    }).then(response => {
        return response["data"];
    })

export const unCollectArticle = (id) =>
    request({
        url: '/article/' + id + '/unCollect',
        method: 'PATCH',
    }).then(response => {
        return response["data"];
    })

export const tippingArticle = (id, volume) =>
    request({
        url: '/article/' + id + '/tipping',
        method: 'POST',
        params: {
            volume: volume,
        }
    }).then(response => {
        return response["data"];
    })

export const forwardArticle = (id) =>
    request({
        url: '/article/' + id + '/forward',
        method: 'PATCH',
    }).then(response => {
        return response["data"];
    })