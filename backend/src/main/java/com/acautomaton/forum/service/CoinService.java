package com.acautomaton.forum.service;

import cn.hutool.json.JSONUtil;
import com.acautomaton.forum.entity.CoinRecord;
import com.acautomaton.forum.entity.Recharge;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.*;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.mapper.CoinRecordMapper;
import com.acautomaton.forum.mapper.RechargeMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.util.AlipayService;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.GoodsDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CoinService {
    UserMapper userMapper;
    CoinRecordMapper coinRecordMapper;
    RechargeMapper rechargeMapper;
    AlipayService alipayService;
    MessageService messageService;

    public CoinService(UserMapper userMapper, CoinRecordMapper coinRecordMapper, RechargeMapper rechargeMapper,
                       AlipayService alipayService, MessageService messageService) {
        this.userMapper = userMapper;
        this.coinRecordMapper = coinRecordMapper;
        this.rechargeMapper = rechargeMapper;
        this.alipayService = alipayService;
        this.messageService = messageService;
    }

    public Integer getCoinsByUid(Integer uid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getCoins);
        queryWrapper.eq(User::getUid, uid);
        return userMapper.selectOne(queryWrapper).getCoins();
    }

    public PageHelperVO<CoinRecord> getCoinRecordList(Integer uid, Integer pageNumber, Integer pageSize) {
        LambdaQueryWrapper<CoinRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CoinRecord::getUid, uid);
        queryWrapper.select(CoinRecord::getId, CoinRecord::getType, CoinRecord::getCoinVolume, CoinRecord::getCoinBalance,
                CoinRecord::getProject, CoinRecord::getComment, CoinRecord::getStatus, CoinRecord::getUpdateTime);
        queryWrapper.orderByDesc(CoinRecord::getUpdateTime);
        return new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize < 40 ? pageSize : 40)
                        .doSelectPageInfo(() -> coinRecordMapper.selectList(queryWrapper))
        );
    }

    public CoinRecord getCoinRecordById(Integer coinRecordId, Integer uid) {
        LambdaQueryWrapper<CoinRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CoinRecord::getId, coinRecordId);
        queryWrapper.select(CoinRecord::getId, CoinRecord::getUid, CoinRecord::getType, CoinRecord::getCoinVolume, CoinRecord::getCoinBalance,
                CoinRecord::getProject, CoinRecord::getComment, CoinRecord::getStatus, CoinRecord::getCreateTime, CoinRecord::getUpdateTime);
        CoinRecord coinRecord = coinRecordMapper.selectOne(queryWrapper);
        if (coinRecord == null || !coinRecord.getUid().equals(uid)) {
            throw new ForumExistentialityException("交易不存在");
        }
        return coinRecord;
    }

    @Transactional(rollbackFor = Exception.class)
    public String buyCoins(Integer uid, Integer coins) throws AlipayApiException {
        if (alreadyHasBuyingCoinsOrderNotPay(uid)) {
            throw new ForumExistentialityException("有未支付的订单，请前往订单列表继续支付或取消订单");
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String price = String.format("%.2f", coins / 100.0);
        String comment = "";
        Date now = new Date();
        log.info("用户 {} 发起购买 {}AC币 (RMB {}) 的订单请求: {}", uid, coins, price, uuid);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUid, uid);
        User user = userMapper.selectOne(queryWrapper);
        CoinRecord coinRecord = new CoinRecord(null, uid, CoinRecordType.RECHARGE, coins, user.getCoins(), "AC币充值", "",
                CoinRecordStatus.RUNNING, now, now, 0);
        coinRecordMapper.insert(coinRecord);
        String subject = "AC币充值";
        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setGoodsName(coins + "AC币");
        goodsDetail.setPrice(price);
        String systemInfo = JSONUtil.toJsonStr(Map.of("goodsDetail", goodsDetail));
        rechargeMapper.insert(new Recharge(null, uid, uuid, null, RechargeChannel.ALI_PAY, RechargeType.RECHARGE_AC_COIN,
                RechargeStatus.WAITING, coins, subject, comment, coinRecord.getId(), systemInfo, now, now, 0));
        log.info("用户 {} 发起购买 {}AC币 (RMB {}) 的订单支付宝跳转请求: {}", uid, coins, price, uuid);
        return alipayService.createOrder(uuid, price, subject, "/userCenter/purse/balance", List.of(goodsDetail));
    }

    @Transactional(rollbackFor = Exception.class)
    public void afterPaying(Integer uid, String tradeId) throws AlipayApiException {
        LambdaQueryWrapper<Recharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Recharge::getUid, uid);
        lambdaQueryWrapper.eq(Recharge::getTradeId, tradeId);
        if (rechargeMapper.exists(lambdaQueryWrapper)) {
            return;
        }
        if (alipayService.updateRechargeStatusByTradeId(tradeId) == RechargeStatus.SUCCESS) {
            Recharge recharge = rechargeMapper.selectOne(lambdaQueryWrapper);
            if (recharge != null) {
                if (recharge.getCoinRecordId() != null) {
                    LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    userLambdaUpdateWrapper.eq(User::getUid, uid);
                    Integer balance = userMapper.selectOne(userLambdaUpdateWrapper).getCoins() + recharge.getAmount();
                    userLambdaUpdateWrapper.set(User::getCoins, balance);
                    userMapper.update(userLambdaUpdateWrapper);

                    LambdaUpdateWrapper<CoinRecord> coinRecordLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    coinRecordLambdaUpdateWrapper.eq(CoinRecord::getId, recharge.getCoinRecordId());
                    coinRecordLambdaUpdateWrapper.set(CoinRecord::getStatus, CoinRecordStatus.SUCCESS);
                    coinRecordLambdaUpdateWrapper.set(CoinRecord::getCoinBalance, balance);
                    coinRecordLambdaUpdateWrapper.set(CoinRecord::getUpdateTime, new Date());
                    coinRecordMapper.update(coinRecordLambdaUpdateWrapper);
                }
                sendBuyCoinsSuccessfullyMessage(uid, recharge.getAmount());
                log.info("[Sync]用户 {} 购买 {} AC币支付成功 (alipay {})", uid, recharge.getAmount(), recharge.getTradeId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean refreshPayingStatusByUid(Integer uid) throws AlipayApiException {
        LambdaQueryWrapper<Recharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Recharge::getUid, uid);
        lambdaQueryWrapper.eq(Recharge::getType, RechargeType.RECHARGE_AC_COIN);
        lambdaQueryWrapper.eq(Recharge::getStatus, RechargeStatus.WAITING);
        Recharge recharge = rechargeMapper.selectOne(lambdaQueryWrapper);
        if (recharge == null) {
            return false;
        }
        String targetUuid = recharge.getUuid();
        Integer coinRecordId = recharge.getCoinRecordId();
        if (alipayService.updateRechargeStatusByRechargeUuid(targetUuid) == RechargeStatus.SUCCESS) {
            lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Recharge::getUuid, targetUuid);
            recharge = rechargeMapper.selectOne(lambdaQueryWrapper);
            if (coinRecordId != null) {
                LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                userLambdaUpdateWrapper.eq(User::getUid, uid);
                Integer balance = userMapper.selectOne(userLambdaUpdateWrapper).getCoins() + recharge.getAmount();
                userLambdaUpdateWrapper.set(User::getCoins, balance);
                userMapper.update(userLambdaUpdateWrapper);

                LambdaUpdateWrapper<CoinRecord> coinRecordLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                coinRecordLambdaUpdateWrapper.eq(CoinRecord::getId, recharge.getCoinRecordId());
                coinRecordLambdaUpdateWrapper.set(CoinRecord::getStatus, CoinRecordStatus.SUCCESS);
                coinRecordLambdaUpdateWrapper.set(CoinRecord::getCoinBalance, balance);
                coinRecordLambdaUpdateWrapper.set(CoinRecord::getUpdateTime, new Date());
                coinRecordMapper.update(coinRecordLambdaUpdateWrapper);
            }
            sendBuyCoinsSuccessfullyMessage(uid, recharge.getAmount());
            log.info("[Async]用户 {} 购买 {} AC币支付成功 (alipay {})", uid, recharge.getAmount(), recharge.getTradeId());
            return true;
        }
        return false;
    }

    private boolean alreadyHasBuyingCoinsOrderNotPay(Integer uid) {
        LambdaQueryWrapper<Recharge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Recharge::getUid, uid);
        queryWrapper.eq(Recharge::getType, RechargeType.RECHARGE_AC_COIN);
        queryWrapper.eq(Recharge::getStatus, RechargeStatus.WAITING);
        return rechargeMapper.exists(queryWrapper);
    }

    private void sendBuyCoinsSuccessfullyMessage(Integer uid, Integer coins) {
        messageService.createMessage(uid, "充值成功: " + coins + " AC币", MessageType.RECHARGE, "感谢您的支持", "");
    }
}
