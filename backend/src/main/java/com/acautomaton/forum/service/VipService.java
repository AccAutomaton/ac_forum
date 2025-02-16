package com.acautomaton.forum.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.acautomaton.forum.entity.CoinRecord;
import com.acautomaton.forum.entity.Recharge;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.entity.Vip;
import com.acautomaton.forum.enumerate.*;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.mapper.CoinRecordMapper;
import com.acautomaton.forum.mapper.RechargeMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.mapper.VipMapper;
import com.acautomaton.forum.service.util.AlipayService;
import com.acautomaton.forum.vo.vip.BuyVipVO;
import com.acautomaton.forum.vo.vip.GetPriceVO;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.GoodsDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class VipService {
    VipMapper vipMapper;
    UserMapper userMapper;
    RechargeMapper rechargeMapper;
    CoinRecordMapper coinRecordMapper;
    AlipayService alipayService;
    MessageService messageService;

    @Autowired
    public VipService(VipMapper vipMapper, UserMapper userMapper, AlipayService alipayService,
                      MessageService messageService, RechargeMapper rechargeMapper, CoinRecordMapper coinRecordMapper) {
        this.vipMapper = vipMapper;
        this.userMapper = userMapper;
        this.alipayService = alipayService;
        this.rechargeMapper = rechargeMapper;
        this.coinRecordMapper = coinRecordMapper;
        this.messageService = messageService;
    }

    public Vip getVipByUid(int uid) {
        QueryWrapper<Vip> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        if (!vipMapper.exists(queryWrapper)) {
            throw new ForumExistentialityException("用户不存在");
        }
        return vipMapper.selectOne(queryWrapper);
    }

    public GetPriceVO getPriceForTargetVip(Integer uid, VipType targetVip) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(userQueryWrapper);
        QueryWrapper<Vip> vipQueryWrapper = new QueryWrapper<>();
        vipQueryWrapper.eq("uid", uid);
        Vip vip = vipMapper.selectOne(vipQueryWrapper);

        Integer ownCoins = user.getCoins();
        Integer deduction = ownCoins < targetVip.getPrice() ? ownCoins : targetVip.getPrice();
        if (targetVip == VipType.NONE) {
            throw new ForumIllegalAccountException("大众会员无需购买");
        }
        if (vip.getVipType() == VipType.NONE) {
            return new GetPriceVO(ownCoins, deduction, targetVip.getPrice() - deduction, targetVip.getPrice(), addDays(new Date(), targetVip.getDays()));
        } else if (targetVip == vip.getVipType()) {
            return new GetPriceVO(ownCoins, deduction, targetVip.getPrice() - deduction, targetVip.getPrice(), addDays(vip.getExpirationTime(), targetVip.getDays()));
        } else if (targetVip.getIndex() > vip.getVipType().getIndex()) {
            int currentRemainderDays = getTimeDistance(new Date(), vip.getExpirationTime());
            double targetPrice = (targetVip.getPrice() - (vip.getVipType().getPrice() * (currentRemainderDays / (vip.getVipType().getDays() * 1.0))));
            targetPrice = targetPrice < 0 ? 0 : targetPrice;
            deduction = ownCoins < targetPrice ? ownCoins : (int) targetPrice;
            return new GetPriceVO(ownCoins, deduction, (int) targetPrice - deduction, (int) targetPrice, addDays(new Date(), targetVip.getDays()));
        }
        throw new ForumIllegalAccountException("不能降级会员");
    }

    @Transactional(rollbackFor = Exception.class)
    public BuyVipVO buyVip(Integer uid, VipType vipType, Integer mode) throws AlipayApiException {
        if (alreadyHasBuyingVipOrderNotPay(uid)) {
            throw new ForumExistentialityException("有未支付的订单，请前往订单列表继续支付或取消订单");
        }
        GetPriceVO priceVO = getPriceForTargetVip(uid, vipType);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String price = switch (mode) {
            case 0 -> String.valueOf(priceVO.getPriceWithCoins() * 1.0 / 100);
            case 1 -> String.valueOf(priceVO.getPriceWithoutCoins() * 1.0 / 100);
            default -> throw new ForumIllegalArgumentException("参数非法");
        };
        String comment = "";
        Date now = new Date();
        log.info("用户 {} 发起购买 {} (RMB {}) 的订单请求: {}", uid, vipType.getValue(), price, uuid);
        Integer coinRecordId = null;
        if (mode == 0 && priceVO.getPayCoins() > 0) {
            comment = "使用 " + priceVO.getPayCoins() + " AC币抵扣 " + priceVO.getPayCoins() * 1.0 / 100 + " RMB";
            LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getUid, uid);
            Integer coins = userMapper.selectOne(lambdaUpdateWrapper).getCoins();
            CoinRecord coinRecord = new CoinRecord(null, uid, CoinRecordType.BUY_VIP_PAYOUT, priceVO.getPayCoins(), coins, vipType.getValue(), "抵扣 " + priceVO.getPayCoins() * 1.0 / 100 + " RMB", CoinRecordStatus.RUNNING, now, now, 0);
            coinRecordMapper.insert(coinRecord);
            coinRecordId = coinRecord.getId();
            lambdaUpdateWrapper.set(User::getCoins, coins - priceVO.getPayCoins());
            userMapper.update(lambdaUpdateWrapper);
        }
        String systemInfo = JSONUtil.toJsonStr(Map.of("vipTypeIndex", vipType.getIndex(), "targetDate", priceVO.getTargetExpireDate()));
        if (Double.parseDouble(price) <= 0.0) {
            if (coinRecordId != null) {
                LambdaUpdateWrapper<CoinRecord> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(CoinRecord::getId, coinRecordId);
                lambdaUpdateWrapper.set(CoinRecord::getStatus, CoinRecordStatus.SUCCESS);
                coinRecordMapper.update(lambdaUpdateWrapper);
            }
            rechargeMapper.insert(new Recharge(null, uid, uuid, null, RechargeChannel.ALI_PAY, RechargeType.BUY_VIP, RechargeStatus.SUCCESS, (int) (Double.parseDouble(price) * 100), vipType.getValue(), comment, coinRecordId, systemInfo, now, now, 0));
            LambdaUpdateWrapper<Vip> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Vip::getUid, uid);
            lambdaUpdateWrapper.set(Vip::getVipType, vipType);
            lambdaUpdateWrapper.set(Vip::getExpirationTime, priceVO.getTargetExpireDate());
            vipMapper.update(lambdaUpdateWrapper);
            sendBuyVipSuccessfullyMessage(uid, vipType, priceVO.getTargetExpireDate());
            return new BuyVipVO(false, null);
        }
        String subject = "AC论坛VIP - " + vipType.getValue();
        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setGoodsName(vipType.getValue());
        goodsDetail.setPrice(price);
        rechargeMapper.insert(new Recharge(null, uid, uuid, null, RechargeChannel.ALI_PAY, RechargeType.BUY_VIP, RechargeStatus.WAITING, (int) (Double.parseDouble(price) * 100), vipType.getValue(), comment, coinRecordId, systemInfo, now, now, 0));
        log.info("用户 {} 发起购买 {} (RMB {}) 的订单支付宝跳转请求: {}", uid, vipType.getValue(), price, uuid);
        return new BuyVipVO(true, alipayService.createOrder(uuid, price, subject, "/vip", List.of(goodsDetail)));
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
                    LambdaUpdateWrapper<CoinRecord> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper.eq(CoinRecord::getId, recharge.getCoinRecordId());
                    lambdaUpdateWrapper.set(CoinRecord::getStatus, CoinRecordStatus.SUCCESS);
                    lambdaUpdateWrapper.set(CoinRecord::getUpdateTime, new Date());
                    coinRecordMapper.update(lambdaUpdateWrapper);
                }
                Map<String, Object> systemInfo = JSONUtil.toBean(recharge.getSystemInfo(), Map.class);
                VipType targetVipType = VipType.getById((Integer) systemInfo.get("vipTypeIndex"));
                Date targetDate = DateUtil.date((Long) systemInfo.get("targetDate"));
                LambdaUpdateWrapper<Vip> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(Vip::getUid, uid);
                lambdaUpdateWrapper.set(Vip::getVipType, targetVipType);
                lambdaUpdateWrapper.set(Vip::getExpirationTime, targetDate);
                vipMapper.update(lambdaUpdateWrapper);
                sendBuyVipSuccessfullyMessage(uid, targetVipType, targetDate);
                log.info("[Sync]用户 {} 购买 {} 支付成功 (alipay {})", uid, targetVipType.getValue(), recharge.getTradeId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean refreshPayingStatusByUid(Integer uid) throws AlipayApiException {
        LambdaQueryWrapper<Recharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Recharge::getUid, uid);
        lambdaQueryWrapper.eq(Recharge::getStatus, RechargeStatus.WAITING);
        Recharge recharge = rechargeMapper.selectOne(lambdaQueryWrapper);
        if (recharge == null) {
            return false;
        }
        String targetUuid = recharge.getUuid();
        Integer coinRecordId = recharge.getCoinRecordId();
        Map<String, Object> systemInfo = JSONUtil.toBean(recharge.getSystemInfo(), Map.class);
        if (alipayService.updateRechargeStatusByRechargeUuid(targetUuid) == RechargeStatus.SUCCESS) {
            if (coinRecordId != null) {
                LambdaUpdateWrapper<CoinRecord> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(CoinRecord::getId, coinRecordId);
                lambdaUpdateWrapper.set(CoinRecord::getStatus, CoinRecordStatus.SUCCESS);
                lambdaUpdateWrapper.set(CoinRecord::getUpdateTime, new Date());
                coinRecordMapper.update(lambdaUpdateWrapper);
            }
            VipType targetVipType = VipType.getById((Integer) systemInfo.get("vipTypeIndex"));
            Date targetDate = DateUtil.date((Long) systemInfo.get("targetDate"));
            LambdaUpdateWrapper<Vip> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Vip::getUid, uid);
            lambdaUpdateWrapper.set(Vip::getVipType, targetVipType);
            lambdaUpdateWrapper.set(Vip::getExpirationTime, targetDate);
            vipMapper.update(lambdaUpdateWrapper);
            sendBuyVipSuccessfullyMessage(uid, targetVipType, targetDate);
            log.info("[Async]用户 {} 购买 {} 支付成功 (alipay {})", uid, targetVipType.getValue(), recharge.getTradeId());
        }
        return true;
    }

    private void sendBuyVipSuccessfullyMessage(Integer uid, VipType vipType, Date targetDate) {
        String prettyTargetDate = DateUtil.formatDate(targetDate);
        messageService.createMessage(uid, "感谢您购买" + vipType.getValue() + "!", MessageType.RECHARGE,
                "会员到期时间: " + prettyTargetDate, "");
    }

    private boolean alreadyHasBuyingVipOrderNotPay(Integer uid) {
        LambdaQueryWrapper<Recharge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Recharge::getUid, uid);
        queryWrapper.eq(Recharge::getType, RechargeType.BUY_VIP);
        queryWrapper.eq(Recharge::getStatus, RechargeStatus.WAITING);
        return rechargeMapper.exists(queryWrapper);
    }

    private static Date addDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static int getTimeDistance(Date beginDate, Date endDate) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(beginDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        long beginTime = beginCalendar.getTime().getTime();
        long endTime = endCalendar.getTime().getTime();
        int betweenDays = (int) ((endTime - beginTime) / (1000 * 60 * 60 * 24));
        endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);
        endCalendar.add(Calendar.DAY_OF_MONTH, -1);
        if (beginCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)) {
            return betweenDays + 1;
        } else {
            return betweenDays;
        }
    }
}
