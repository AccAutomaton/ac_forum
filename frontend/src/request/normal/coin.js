import request from "@/request/normal/index.js";

export const getCoins = () => request({
    url: '/coin',
    method: 'GET',
}).then(response => response["data"])

export const buyCoins = (coins) =>
    request({
        url: '/coin/buy',
        method: 'POST',
        params: {
            coins: coins,
        }
    }).then(response => response["data"])

export const afterPaying = (tradeId) =>
    request({
        url: '/coin/payed',
        method: 'POST',
        params: {
            tradeId: tradeId,
        }
    }).then(response => response["data"])

export const refreshPayingStatus = () =>
    request({
        url: '/coin/refresh',
        method: 'POST',
    }).then(response => response["data"])

export const getCoinRecordList = (pageNumber, pageSize) =>
    request({
        url: '/coin/record/list',
        method: 'GET',
        params: {
            pageNumber: pageNumber,
            pageSize: pageSize,
        }
    }).then(response => response["data"])

export const getCoinRecordById = (coinRecordId) =>
    request({
        url: '/coin/record/' + coinRecordId,
        method: 'GET',
    }).then(response => response["data"])