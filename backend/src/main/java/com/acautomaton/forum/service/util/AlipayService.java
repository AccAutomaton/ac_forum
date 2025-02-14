package com.acautomaton.forum.service.util;

import com.acautomaton.forum.entity.Recharge;
import com.acautomaton.forum.enumerate.RechargeStatus;
import com.acautomaton.forum.mapper.RechargeMapper;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    RechargeMapper rechargeMapper;

    @Autowired
    public AlipayService(RechargeMapper rechargeMapper) {
        this.rechargeMapper = rechargeMapper;
    }

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

    @Transactional(rollbackFor = Exception.class)
    public RechargeStatus updateRechargeStatusByRechargeUuid(String rechargeUuid) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(rechargeUuid);
        request.setBizModel(model);
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        String tradeId = response.getTradeNo();
        LambdaUpdateWrapper<Recharge> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Recharge::getUuid, rechargeUuid);
        lambdaUpdateWrapper.set(Recharge::getTradeId, tradeId);
        RechargeStatus rechargeStatus = switch (response.getTradeStatus()) {
            case "TRADE_SUCCESS", "TRADE_FINISHED" -> RechargeStatus.SUCCESS;
            default -> RechargeStatus.WAITING;
        };
        lambdaUpdateWrapper.set(Recharge::getStatus, rechargeStatus);
        lambdaUpdateWrapper.set(Recharge::getUpdateTime, new Date());
        rechargeMapper.update(lambdaUpdateWrapper);
        return rechargeStatus;
    }

    @Transactional(rollbackFor = Exception.class)
    public RechargeStatus updateRechargeStatusByTradeId(String tradeId) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setTradeNo(tradeId);
        request.setBizModel(model);
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        String rechargeUuid = response.getOutTradeNo();
        LambdaUpdateWrapper<Recharge> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Recharge::getUuid, rechargeUuid);
        lambdaUpdateWrapper.set(Recharge::getTradeId, tradeId);
        RechargeStatus rechargeStatus = switch (response.getTradeStatus()) {
            case "TRADE_SUCCESS", "TRADE_FINISHED" -> RechargeStatus.SUCCESS;
            default -> RechargeStatus.WAITING;
        };
        lambdaUpdateWrapper.set(Recharge::getStatus, rechargeStatus);
        lambdaUpdateWrapper.set(Recharge::getUpdateTime, new Date());
        rechargeMapper.update(lambdaUpdateWrapper);
        return rechargeStatus;
    }
}
