import request from "@/request/normal/index.js";

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
    }).then(response => response["data"])

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
    }).then(response => response["data"])

export const getArticleImageUploadAuthorization = () =>
    request({
        url: '/article/image/authorization/upload',
        method: 'GET',
    }).then(response => response["data"])

export const createArticle = (topic, title, content) =>
    request({
        url: '/article',
        method: 'PUT',
        data: {
            topic: topic,
            title: title,
            content: content,
        }
    }).then(response => response["data"])

export const getArticleById = (id) =>
    request({
        url: '/article/' + id,
        method: 'GET',
    }).then(response => response["data"])

export const updateArticleById = (articleId, topic, title, content) =>
    request({
        url: '/article/' + articleId,
        method: 'PATCH',
        data: {
            topic: topic,
            title: title,
            content: content,
        }
    }).then(response => response["data"])

export const thumbsUpArticle = (id) =>
    request({
        url: '/article/' + id + '/thumbsUp',
        method: 'PUT',
    }).then(response => response["data"])

export const unThumbsUpArticle = (id) =>
    request({
        url: '/article/' + id + '/thumbsUp',
        method: 'DELETE',
    }).then(response => response["data"])

export const collectArticle = (id) =>
    request({
        url: '/article/' + id + '/collect',
        method: 'PUT',
    }).then(response => response["data"])

export const unCollectArticle = (id) =>
    request({
        url: '/article/' + id + '/collect',
        method: 'DELETE',
    }).then(response => response["data"])

export const tippingArticle = (id, volume) =>
    request({
        url: '/article/' + id + '/tipping',
        method: 'POST',
        params: {
            volume: volume,
        }
    }).then(response => response["data"])

export const forwardArticle = (id) =>
    request({
        url: '/article/' + id + '/forward',
        method: 'PATCH',
    }).then(response => response["data"])

export const getCommentListByArticleId = (id, latest = false, pageNumber, pageSize) =>
    request({
        url: '/article/' + id + '/comment/list',
        method: 'GET',
        params: {
            latest: latest,
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then(response => response["data"])

export const createCommentByArticleId = (articleId, content, targetCommentId = null) =>
    request({
        url: '/article/' + articleId + '/comment',
        method: 'PUT',
        data: {
            content: content,
            targetCommentId: targetCommentId,
        }
    }).then(response => response["data"])

export const getOwnArticlesVisitsTopX = (x) =>
    request({
        url: '/article/list/visit',
        method: 'GET',
        params: {
            topX: x,
        }
    }).then(response => response["data"])

export const getOwnArticleList = (pageNumber, pageSize, queryType = 2) =>
    request({
        url: '/article/list/own',
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
        }
    }).then(response => response["data"])

export const deleteArticleById = (id) =>
    request({
        url: '/article/' + id,
        method: 'DELETE',
    }).then(response => response["data"])