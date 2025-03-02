package com.acautomaton.forum.service;

import cn.hutool.json.JSONUtil;
import com.acautomaton.forum.entity.CoinRecord;
import com.acautomaton.forum.entity.Recharge;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CoinRecordStatus;
import com.acautomaton.forum.enumerate.RechargeStatus;
import com.acautomaton.forum.enumerate.RechargeType;
import com.acautomaton.forum.exception.ForumException;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.mapper.CoinRecordMapper;
import com.acautomaton.forum.mapper.RechargeMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.util.AlipayService;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.GoodsDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RechargeService {
    RechargeMapper rechargeMapper;
    AlipayService alipayService;
    CoinRecordMapper coinRecordMapper;
    UserMapper userMapper;

    @Autowired
    public RechargeService(RechargeMapper rechargeMapper, AlipayService alipayService,
                           CoinRecordMapper coinRecordMapper, UserMapper userMapper) {
        this.rechargeMapper = rechargeMapper;
        this.alipayService = alipayService;
        this.coinRecordMapper = coinRecordMapper;
        this.userMapper = userMapper;
    }

    public PageHelperVO<Recharge> getRechargeList(Integer uid, Integer pageNumber, Integer pageSize) {
        LambdaQueryWrapper<Recharge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Recharge::getUid, uid);
        queryWrapper.select(Recharge::getId, Recharge::getChannel, Recharge::getType, Recharge::getStatus, Recharge::getAmount, Recharge::getSubject,
                Recharge::getComment, Recharge::getUpdateTime);
        queryWrapper.orderByDesc(Recharge::getUpdateTime);
        return new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize < 40 ? pageSize : 40)
                        .doSelectPageInfo(() -> rechargeMapper.selectList(queryWrapper))
        );
    }

    public Recharge getRechargeById(Integer id, Integer uid) {
        LambdaQueryWrapper<Recharge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Recharge::getId, id);
        queryWrapper.select(Recharge::getId, Recharge::getUid, Recharge::getUuid, Recharge::getTradeId, Recharge::getChannel,
                Recharge::getType, Recharge::getStatus, Recharge::getAmount, Recharge::getSubject, Recharge::getComment,
                Recharge::getCreateTime, Recharge::getUpdateTime);
        Recharge recharge = rechargeMapper.selectOne(queryWrapper);
        if (recharge == null || !recharge.getUid().equals(uid)) {
            throw new ForumExistentialityException("交易不存在");
        }
        return recharge;
    }

    public String continueRecharge(Integer rechargeId, Integer uid) throws AlipayApiException {
        LambdaQueryWrapper<Recharge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Recharge::getUid, uid);
        queryWrapper.eq(Recharge::getId, rechargeId);
        Recharge recharge = rechargeMapper.selectOne(queryWrapper);
        if (recharge == null || !recharge.getUid().equals(uid)) {
            throw new ForumExistentialityException("交易不存在");
        }
        if (recharge.getStatus() != RechargeStatus.WAITING) {
            throw new ForumException("交易已结束");
        }
        GoodsDetail goodsDetail = JSONUtil.parseObj(recharge.getSystemInfo()).get("goodsDetail", GoodsDetail.class);
        String returnUrlAppend = switch (recharge.getType()) {
            case RechargeType.RECHARGE_AC_COIN -> "/userCenter/purse/balance";
            case RechargeType.BUY_VIP -> "/vip";
            default -> "";
        };
        String price = String.format("%.2f", recharge.getAmount() / 100.0);
        log.info("用户 {} 发起继续支付订单序号 {} 的支付宝跳转请求", uid, rechargeId);
        return alipayService.createOrder(recharge.getUuid(), price, recharge.getSubject(), returnUrlAppend, List.of(goodsDetail));
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelRecharge(Integer rechargeId, Integer uid) {
        LambdaQueryWrapper<Recharge> rechargeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rechargeLambdaQueryWrapper.eq(Recharge::getUid, uid);
        rechargeLambdaQueryWrapper.eq(Recharge::getId, rechargeId);
        Recharge recharge = rechargeMapper.selectOne(rechargeLambdaQueryWrapper);
        if (recharge == null || !recharge.getUid().equals(uid)) {
            throw new ForumExistentialityException("交易不存在");
        }
        if (recharge.getStatus() != RechargeStatus.WAITING) {
            throw new ForumException("交易已结束");
        }
        Date now = new Date();
        recharge.setStatus(RechargeStatus.CANCEL);
        recharge.setUpdateTime(now);
        rechargeMapper.updateById(recharge);
        if (recharge.getCoinRecordId() != null) {
            CoinRecord coinRecord = coinRecordMapper.selectById(recharge.getCoinRecordId());
            coinRecord.setStatus(CoinRecordStatus.CANCEL);
            coinRecord.setCoinBalance(coinRecord.getCoinBalance() + coinRecord.getCoinVolume());
            coinRecord.setUpdateTime(now);
            coinRecordMapper.updateById(coinRecord);
            User user = userMapper.selectById(uid);
            user.setCoins(user.getCoins() + coinRecord.getCoinVolume());
            userMapper.updateById(user);
        }
        log.info("用户 {} 取消支付订单序号 {}", uid, rechargeId);
    }
}
