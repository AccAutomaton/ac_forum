import request from "@/request/normal/index.js";

export const getRechargeList = (pageNumber, pageSize) => request({
    url: '/recharge/list',
    method: 'GET',
    params: {
        pageNumber: pageNumber,
        pageSize: pageSize
    }
}).then(response => response["data"])

export const getRechargeById = (rechargeId) => request({
    url: '/recharge/' + rechargeId,
    method: 'GET',
}).then(response => response["data"])

export const continueRechargeById = (rechargeId) => request({
    url: '/recharge/continue',
    method: 'POST',
    params: {
        rechargeId: rechargeId,
    }
}).then(response => response["data"])

export const cancelRechargeById = (rechargeId) => request({
    url: '/recharge/cancel',
    method: 'PATCH',
    params: {
        rechargeId: rechargeId,
    }
}).then(response => response["data"])