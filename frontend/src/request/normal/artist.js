import request from "@/request/normal/index.js";

export const getArtistStatisticDataByUid = (uid) =>
    request({
        url: `/artist/${uid}/statistic`,
        method: 'GET',
    }).then(response => response["data"])

export const getArtistArticleListByUid = (uid, pageNumber, pageSize, queryType) =>
    request({
        url: `/artist/${uid}/article`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
        }
    }).then(response => response["data"])

export const getArtistTopicListByUid = (uid, pageNumber, pageSize, queryType) =>
    request({
        url: `/artist/${uid}/topic`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            queryType: queryType,
        }
    }).then(response => response["data"])

export const getArtistThumbsUpListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/thumbsUp`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then(response => response["data"])

export const getArtistCollectionListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/collection`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then(response => response["data"])

export const getArtistFollowsListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/follows`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then(response => response["data"])

export const getArtistFansListByUid = (uid, pageNumber, pageSize) =>
    request({
        url: `/artist/${uid}/fans`,
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then(response => response["data"])

export const getDashboardData = () =>
    request({
        url: `/artist/dashboard`,
        method: 'GET',
    }).then(response => response["data"])

export const getTippingIncreamentNearly7Days = () =>
    request({
        url: `/artist/tipping/increament`,
        method: 'GET',
    }).then(response => response["data"])