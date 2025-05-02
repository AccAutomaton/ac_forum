import request from "@/request/normal/index.js";

export const getVip = () =>
    request({
        url: '/vip',
        method: 'GET',
    }).then(response => response["data"])

export const getVipPrice = (targetVipIndex) =>
    request({
        url: '/vip/price',
        method: 'GET',
        params: {
            targetVipIndex: targetVipIndex,
        }
    }).then(response => response["data"])

export const buyVip = (targetVipIndex, mode) =>
    request({
        url: '/vip/buy',
        method: 'POST',
        data: {
            targetVipIndex: targetVipIndex,
            mode: mode,
        }
    }).then(response => response["data"])

export const afterPaying = (tradeId) =>
    request({
        url: '/vip/buy/payed',
        method: 'POST',
        params: {
            tradeId: tradeId,
        }
    }).then(response => response["data"])

export const refreshPayingStatus = () => 
    request({
        url: '/vip/pay/refresh',
        method: 'POST',
    }).then(response => response["data"])