import request from "@/request/index.js";

export const getCoins = () => request({
    url: '/coin',
    method: 'GET',
}).then((response) => {
    return response["data"];
})

export const buyCoins = (coins) =>
    request({
        url: '/coin/buy',
        method: 'POST',
        params: {
            coins: coins,
        }
    }).then(response => {
        return response["data"];
    })

export const afterPaying = (tradeId) =>
    request({
        url: '/coin/payed',
        method: 'POST',
        params: {
            tradeId: tradeId,
        }
    }).then(response => {
        return response["data"];
    })

export const refreshPayingStatus = () =>
    request({
        url: '/coin/refresh',
        method: 'POST',
    }).then(response => {
        return response["data"];
    })