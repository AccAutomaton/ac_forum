import store from "@/store/index.js";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {ElNotification} from "element-plus";
import router from "@/router/index.js";

export const GetAuthorizationCode = () => {
    if (store.getters.getAuthorizationCode !== "") {
        return store.getters.getAuthorizationCode;
    }
    return useStorage("Authorization", "").value
}

const request = axios.create({
    baseURL: import.meta.env.VITE_API_HOST,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest"
    }
})

request.interceptors.request.use(
    function (config) {
        config.headers["Authorization"] = GetAuthorizationCode();
        return config;
    },
    function (error) {
        ElNotification({
            title: '客户端错误',
            message: '向服务器请求数据时发生异常',
            type: 'error'
        });
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    function (response) {
        if (response.status === 403) {
            ElNotification({
                title: '错误',
                message: '非法操作',
                type: 'error'
            })
            return null;
        }
        switch (response.data["status"]["code"]) {
            case 200:
                return response.data;
            case 401:
                ElNotification({
                    title: '错误',
                    message: '登录已失效，请重新登录',
                    type: 'warning'
                })
                localStorage.setItem("Authorization", "");
                router.push('/login').then(() => {
                });
                return response.data;
            case 403:
                ElNotification({
                    title: '错误',
                    message: response.data["message"],
                    type: 'warning'
                });
                return response.data;
            case 503:
                ElNotification({
                    title: '请求速度过快',
                    message: response.data["message"],
                    type: 'warning'
                })
                return response.data;
            default:
                ElNotification({
                    title: '未知响应',
                    message: '无法解析服务器的响应',
                    type: 'error'
                })
                return response.data;
        }
    },
    function (error) {
        ElNotification({
            title: '服务器错误',
            message: '服务器发生内部错误',
            type: 'error'
        });
        return Promise.reject(error)
    }
)

export default request;