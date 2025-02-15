package com.acautomaton.forum.vo.cos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tencent.cloud.Credentials;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CosAuthorizationVO {

    String secretId;
    String secretKey;
    String securityToken;
    Long startTime;
    Long expiredTime;
    String bucket;
    String region;
    String prefix;
    String key;

    public static CosAuthorizationVO prefixAuthorization(Credentials credentials, Integer expiredSeconds, String bucket, String region, String prefix) {
        Date date = new Date();
        return new CosAuthorizationVO(
                credentials.tmpSecretId,
                credentials.tmpSecretKey,
                credentials.sessionToken,
                date.getTime() / 1000L,
                date.getTime() / 1000L + expiredSeconds,
                bucket,
                region,
                prefix,
                null
        );
    }

    public static CosAuthorizationVO keyAuthorization(Credentials credentials, Integer expiredSeconds, String bucket, String region, String key) {
        Date date = new Date();
        return new CosAuthorizationVO(
                credentials.tmpSecretId,
                credentials.tmpSecretKey,
                credentials.sessionToken,
                date.getTime() / 1000L,
                date.getTime() / 1000L + expiredSeconds,
                bucket,
                region,
                null,
                key
        );
    }

    public static CosAuthorizationVO publicResourcesAuthorization(Credentials credentials, Integer expiredSeconds, String bucket, String region) {
        Date date = new Date();
        return new CosAuthorizationVO(
                credentials.tmpSecretId,
                credentials.tmpSecretKey,
                credentials.sessionToken,
                date.getTime() / 1000L,
                date.getTime() / 1000L + expiredSeconds,
                bucket,
                region,
                null,
                null
        );
    }
}
