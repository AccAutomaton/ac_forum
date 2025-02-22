import request from "@/request/index.js";

export const queryArticleListOfTopic = (topicId, pageNumber, pageSize, queryType, keyword = "") =>
    request({
        url: 'article/get/list/topic/' + topicId,
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