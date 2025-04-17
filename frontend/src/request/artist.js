import request from "@/request/index.js";

export const getArtistStatisticDataByUid = (uid) =>
    request({
        url: `/artist/${uid}/statistic`,
        method: 'GET',
    }).then((response) => {
        return response["data"];
    })