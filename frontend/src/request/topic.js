import request from "@/request/index.js";

export const queryTopicList = (pageNumber, pageSize, queryType) =>
    request({
        url: '/topic/get/list',
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
        }
    }).then((response) => {
        return response["data"];
    })