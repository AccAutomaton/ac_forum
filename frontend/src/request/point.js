import request from "@/request/index.js";

export const getPoint = () => request({
    url: '/point',
    method: 'GET',
}).then((response) => {
    return response["data"];
})

export const getPointRecordList = (pageNumber, pageSize) => request({
    url: '/point/list',
    method: 'GET',
    params: {
        pageNumber: pageNumber,
        pageSize: pageSize,
    }
}).then((response) => {
    return response["data"];
})

export const getPointRecordById = (pointRecordId) => request({
    url: '/point/' + pointRecordId,
    method: 'GET',
}).then((response) => {
    return response["data"];
})