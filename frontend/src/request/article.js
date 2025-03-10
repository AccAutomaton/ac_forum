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