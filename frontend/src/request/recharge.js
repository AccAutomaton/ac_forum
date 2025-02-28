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