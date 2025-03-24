import request from "@/request/index.js";

export const queryTopicList = (pageNumber, pageSize, queryType, keyword) =>
    request({
        url: '/topic/list',
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
            keyword: keyword,
        }
    }).then((response) => {
        return response["data"];
    })

export const createTopic = (title, description) =>
    request({
        url: '/topic',
        method: 'PUT',
        data: {
            title: title,
            description: description,
        }
    }).then((response) => {
        return response["data"];
    })

export const getTopicById = (id) =>
    request({
        url: '/topic/' + id,
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const deleteTopic = (id) =>
    request({
        url: '/topic',
        method: 'DELETE',
        params: {
            topicId: id,
        }
    }).then((response) => {
        return response["data"];
    })

export const updateTopic = (id, title, description) =>
    request({
        url: '/topic/' + id,
        method: 'PATCH',
        data: {
            title: title,
            description: description,
        }
    }).then((response) => {
        return response["data"];
    })

export const queryTopicIdAndTitleList = (keyword) =>
    request({
        url: '/topic/list/idAndTitle',
        method: 'GET',
        params: {
            keyword: keyword,
        }
    }).then((response) => {
        return response["data"];
    })