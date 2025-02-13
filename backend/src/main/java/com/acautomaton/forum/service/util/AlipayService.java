package com.acautomaton.forum.service.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradePagePayRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AlipayService {
    @Value("${project.alibaba.alipay.appPrivateKey}")
    String appPrivateKey;
    @Value("${project.alibaba.alipay.alipayPublicKey}")
    String alipayPublicKey;
    @Value("${project.alibaba.alipay.serverUrl}")
    String serverUrl;
    @Value("${project.alibaba.alipay.appId}")
    String appId;
    @Value("${project.cors.allow-origin}")
    String returnUrlPrepend;

    AlipayClient alipayClient;

    @PostConstruct
    public void initAlipayClient() throws AlipayApiException {
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(serverUrl);
        config.setAppId(appId);
        config.setPrivateKey(appPrivateKey);
        config.setFormat("json");
        config.setAlipayPublicKey(alipayPublicKey);
        config.setCharset("UTF-8");
        config.setSignType("RSA2");
        alipayClient = new DefaultAlipayClient(config);
        log.info("Alibaba.Alipay -- 连接初始化成功");
    }

    public String createOrder(String uuid, String price, String subject, String returnUrlAppend, List<GoodsDetail> goodsDetailList) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setSubject(subject);
        model.setOutTradeNo(uuid);
        model.setTotalAmount(price);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setGoodsDetail(goodsDetailList);
        request.setBizModel(model);
        request.setReturnUrl(returnUrlPrepend + returnUrlAppend);
        return alipayClient.pageExecute(request).getBody();
    }
}
