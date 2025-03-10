import request from "@/request/index.js";
import COS from "cos-js-sdk-v5";

const publicResourcesCosReadAuthorization = {
    state: {
        cos: null,
        expiredTime: 0,
        bucket: "",
        region: "",
    },
    getters: {
        getPublicResourcesReadCOS: async (state) => {
            if (state.expiredTime === 0 || state.expiredTime - new Date().getTime() / 1000 <= 60 * 5) {
                const data = await request({
                    url: "/cos/authorization/read/public",
                    method: "GET",
                }).then((response) => {
                    return response["data"];
                });
                if (data !== null) {
                    state.cos = new COS({
                        SecretId: data["secretId"],
                        SecretKey: data["secretKey"],
                        SecurityToken: data["securityToken"],
                        StartTime: data["startTime"],
                        ExpiredTime: data["expiredTime"],
                    });
                    state.expiredTime = data["expiredTime"];
                    state.bucket = data["bucket"];
                    state.region = data["region"];
                }
            }
            return state.cos;
        },
        SyncGetPublicResourcesReadCOS: (state) => {
            return state.cos;
        },
        getPublicResourcesBucket: (state) => {
            return state.bucket
        },
        getPublicResourcesRegion: (state) => {
            return state.region
        }
    },
    mutations: {},
    actions: {},
    modules: {},
}

export default publicResourcesCosReadAuthorization;