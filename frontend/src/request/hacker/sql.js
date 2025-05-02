import request from "@/request/normal/index.js";

export const doSelectSql = (sql) =>
    request({
        url: '/hacker/sql/select',
        method: 'POST',
        data: sql
    }).then(response => {
        return response["data"];
    })

export const doUpdateSql = (sql) =>
    request({
        url: '/hacker/sql/update',
        method: 'POST',
        data: sql
    }).then(response => {
        return response["data"];
    })