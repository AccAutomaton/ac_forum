import request from "@/request/normal/index.js";

export const synchronizeEsArticleData = () =>
    request({
        url: '/root/article/elasticSearch/synchronize/fully',
        method: 'POST',
    }).then(response => {
        return response["data"];
    })

export const synchronizeEsTopicData = () =>
    request({
        url: '/root/topic/elasticSearch/synchronize/fully',
        method: 'POST',
    }).then(response => {
        return response["data"];
    })