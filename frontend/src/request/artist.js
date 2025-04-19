import request from "@/request/index.js";

export const getArtistStatisticDataByUid = (uid) =>
    request({
        url: `/artist/${uid}/statistic`,
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })

export const getArtistArticleListByUid = (uid, pageNumber, pageSize, queryType) =>
    request({
        url: `/artist/${uid}/article`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
        }
    }).then((response) => {
        return response["data"];
    })

export const getArtistTopicListByUid = (uid, pageNumber, pageSize, queryType) =>
    request({
        url: `/artist/${uid}/topic`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
        }
    }).then((response) => {
        return response["data"];
    })

export const getArtistThumbsUpListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/thumbsUp`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then((response) => {
        return response["data"];
    })

export const getArtistCollectionListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/collection`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then((response) => {
        return response["data"];
    })

export const getArtistFollowsListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/follows`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then((response) => {
        return response["data"];
    })

export const getArtistFansListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/fans`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then((response) => {
        return response["data"];
    })