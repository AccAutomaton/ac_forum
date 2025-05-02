package com.acautomaton.forum.service.util;

import com.acautomaton.forum.enumerate.CosActions;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.exception.ForumException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.*;
import com.tencent.cloud.cos.util.Jackson;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Getter
@Service
public class CosService {
    @Value("${project.tencent.cos.secretId}")
    String secretId;
    @Value("${project.tencent.cos.secretKey}")
    String secretKey;
    @Value("${project.tencent.cos.bucketName}")
    String bucketName;
    @Value("${project.tencent.cos.region}")
    String region;
    @Value("${project.tencent.cos.bucketResourcePrepend}")
    String bucketResourcePrepend;
    @Value("${project.tencent.cos.objectUrlPrepend}")
    String objectUrlPrepend;
    @Value("${spring.profiles.active}")
    String env;

    COSClient cosClient;

    @Async
    @PostConstruct
    public void initCosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        cosClient = new COSClient(cred, clientConfig);
        log.info("Tencent.COS -- 连接初始化成功");
    }

    public Credentials getCosAccessAuthorization(Integer expireTime, CosActions allowActions, List<String> allowResources) {
        TreeMap<String, Object> configuration = new TreeMap<>();
        configuration.put("secretId", secretId);
        configuration.put("secretKey", secretKey);
        Policy policy = new Policy();
        configuration.put("host", "sts.internal.tencentcloudapi.com");
        if ("dev".equals(env)) {
            configuration.remove("host");
        }
        if (expireTime > 60 * 60 * 2) {
            log.warn("COS临时密钥时长非法: {}", expireTime);
            throw new ForumIllegalArgumentException("系统错误，请稍后再试");
        }
        configuration.put("durationSeconds", expireTime);
        configuration.put("bucket", bucketName);
        configuration.put("region", region);
        Statement statement = new Statement();
        statement.setEffect("allow");
        statement.addActions(allowActions.getActions());
        for (String allowResource : allowResources) {
            statement.addResource(bucketResourcePrepend + allowResource);
        }
        policy.addStatement(statement);
        configuration.put("policy", Jackson.toJsonPrettyString(policy));
        try {
            Response response = CosStsClient.getCredential(configuration);
            log.info("COS临时密钥生成成功: 过期时间 {} min, 允许操作 {}, 允许资源 {}", expireTime / 60, allowActions.getActions(), allowResources);
            return response.credentials;
        }
        catch (IOException e) {
            log.error("向COS服务器请求临时密钥时发生错误: {}", e.getMessage());
            throw new ForumException("系统错误，请稍后再试");
        }
    }

    public CosAuthorizationVO getPublicResourcesReadAuthorization() {
        Integer expireTime = 60 * 60;
        Credentials credentials = getCosAccessAuthorization(expireTime, CosActions.GET_OBJECT, List.of(
                CosFolderPath.AVATAR + "*",
                CosFolderPath.TOPIC_AVATAR + "*",
                CosFolderPath.ARTICLE_IMAGE + "*"
        ));
        return CosAuthorizationVO.publicResourcesAuthorization(credentials, expireTime, bucketName, region);
    }

    public boolean objectExists(String key) {
        try {
            boolean exists = cosClient.doesObjectExist(bucketName, key);
            log.debug("检测文件 {} 存在性: {}", bucketName + "/" + key, exists);
            return exists;
        } catch (CosClientException e) {
            log.error("请求 COS 服务器时发生错误: {}", e.getMessage());
            throw new ForumException("系统错误，请稍后再试");
        }
    }
}
