import COS from "cos-js-sdk-v5";

export const cos = (cosAuthorization) => new COS({
    SecretId: cosAuthorization["secretId"],
    SecretKey: cosAuthorization["secretKey"],
    SecurityToken: cosAuthorization["securityToken"],
    StartTime: cosAuthorization["startTime"],
    ExpiredTime: cosAuthorization["expiredTime"],
})