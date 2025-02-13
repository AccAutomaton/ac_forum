import request from "@/request/index.js";

export const getVip = () =>
    request({
        url: 'vip/get',
        method: 'GET',
    }).then(response => {
        return response["data"];
    })

export const getVipPrice = (targetVipIndex) =>
    request({
        url: 'vip/price',
        method: 'GET',
        params: {
            targetVipIndex: targetVipIndex,
        }
    }).then(response => {
        return response["data"];
    })

export const buyVip = (targetVipIndex, mode) =>
    request({
        url: 'vip/buy',
        method: 'POST',
        data: {
            targetVipIndex: targetVipIndex,
            mode: mode,
        }
    }).then(response => {
        return response["data"];
    })