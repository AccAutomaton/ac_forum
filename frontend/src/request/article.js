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