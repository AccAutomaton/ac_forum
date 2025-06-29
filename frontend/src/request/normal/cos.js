import COS from "cos-js-sdk-v5";
import {ElNotification} from "element-plus";
import store from "@/store/index.js";
import {computed} from "vue";

export const getObjectUrlOfPublicResources = async (key, callback, oringinal = false) => {
    const cos = await store.getters.getPublicResourcesReadCOS;
    cos.getObjectUrl(
        {
            Bucket: store.getters.getPublicResourcesBucket,
            Region: store.getters.getPublicResourcesRegion,
            Key: key,
            QueryString: oringinal ? "" : "imageMogr2/format/webp",
        }, (err, data) => {
            if (err !== null) {
                ElNotification({title: "服务器错误", type: "error", message: "存储服务发生错误"});
            } else {
                callback(data.Url);
            }
        }
    )
}

export const SyncGetObjectUrlOfPublicResources = (key, oringinal = false) => {
    const cos = computed(() => store.getters.SyncGetPublicResourcesReadCOS);
    return cos.value.getObjectUrl({
        Bucket: store.getters.getPublicResourcesBucket,
        Region: store.getters.getPublicResourcesRegion,
        Key: key,
        QueryString: oringinal ? "" : "imageMogr2/format/webp",
    }, () => {})
}

export const getObjectUrl = (cosAuthorization, callback, oringinal = false) => {
    return new COS({
        SecretId: cosAuthorization["secretId"],
        SecretKey: cosAuthorization["secretKey"],
        SecurityToken: cosAuthorization["securityToken"],
        StartTime: cosAuthorization["startTime"],
        ExpiredTime: cosAuthorization["expiredTime"],
    }).getObjectUrl(
        {
            Bucket: cosAuthorization["bucket"],
            Region: cosAuthorization["region"],
            Key: cosAuthorization["key"],
            QueryString: oringinal ? "" : "imageMogr2/format/webp",
        },
        (err, data) => {
            if (err !== null) {
                ElNotification({title: "服务器错误", type: "error", message: "存储服务发生错误"});
            } else {
                callback(data.Url);
            }
        }
    );
}

export const uploadObject = (cosAuthorization, file, callback) => {
    return new COS({
        SecretId: cosAuthorization["secretId"],
        SecretKey: cosAuthorization["secretKey"],
        SecurityToken: cosAuthorization["securityToken"],
        StartTime: cosAuthorization["startTime"],
        ExpiredTime: cosAuthorization["expiredTime"],
    }).uploadFile(
        {
            Bucket: cosAuthorization["bucket"],
            Region: cosAuthorization["region"],
            Key: cosAuthorization["key"],
            Body: file
        },
        (err) => {
            if (err !== null) {
                ElNotification({title: "服务器错误", type: "error", message: "存储服务发生错误"});
            } else {
                callback();
            }
        }
    )
}