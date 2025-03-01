import request from "@/request/index.js";

export const getRechargeList = (pageNumber, pageSize) => request({
    url: '/recharge/get/list',
    method: 'GET',
    params: {
        pageNumber: pageNumber,
        pageSize: pageSize
    }
}).then(response => {
    return response["data"];
})

export const getRechargeById = (rechargeId) => request({
    url: '/recharge/get/' + rechargeId,
    method: 'GET',
}).then(response => {
    return response["data"];
})

export const continueRechargeById = (rechargeId) => request({
    url: '/recharge/continue/' + rechargeId,
    method: 'POST',
}).then(response => {
    return response["data"];
})

export const cancelRechargeById = (rechargeId) => request({
    url: '/recharge/cancel/' + rechargeId,
    method: 'PATCH',
}).then(response => {
    return response["data"];
})