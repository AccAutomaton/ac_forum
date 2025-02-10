import request from "@/request/index.js";

export const queryTopicList = (pageNumber, pageSize, queryType, keyword) =>
    request({
        url: '/topic/get/list',
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
        url: '/topic/create',
        method: 'PUT',
        data: {
            title: title,
            description: description,
        }
    }).then((response) => {
        return response["data"];
    })