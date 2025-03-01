package com.acautomaton.forum.service;

import cn.hutool.json.JSONUtil;
import com.acautomaton.forum.entity.*;
import com.acautomaton.forum.enumerate.*;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.exception.ForumVerifyException;
import com.acautomaton.forum.mapper.CoinRecordMapper;
import com.acautomaton.forum.mapper.RechargeMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.util.AlipayService;
import com.acautomaton.forum.service.util.CaptchaService;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.service.util.EmailService;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.user.GetDetailsVO;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.GoodsDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tencent.cloud.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    EmailService emailService;
    UserMapper userMapper;
    CosService cosService;
    CaptchaService captchaService;
    RechargeMapper rechargeMapper;
    CoinRecordMapper coinRecordMapper;
    PasswordEncoder passwordEncoder;
    AlipayService alipayService;
    MessageService messageService;

    @Autowired
    public UserService(UserMapper userMapper, CosService cosService, CaptchaService captchaService, EmailService emailService,
                       RechargeMapper rechargeMapper, PasswordEncoder passwordEncoder, CoinRecordMapper coinRecordMapper,
                       AlipayService alipayService, MessageService messageService) {
        this.userMapper = userMapper;
        this.cosService = cosService;
        this.captchaService = captchaService;
        this.emailService = emailService;
        this.rechargeMapper = rechargeMapper;
        this.passwordEncoder = passwordEncoder;
        this.coinRecordMapper = coinRecordMapper;
        this.alipayService = alipayService;
        this.messageService = messageService;
    }

    public User getCurrentUser() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return securityUser.getUser();
    }

    public GetNavigationBarInformationVO getNavigationBarInformationByUid(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        return new GetNavigationBarInformationVO(user);
    }

    public String getAvatarByUid(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.select("avatar");
        return userMapper.selectOne(queryWrapper).getAvatar();
    }

    @Transactional(rollbackFor = Exception.class)
    public CosAuthorizationVO getAvatarUpdateAuthorizationByUid(Integer uid) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        Integer expireSeconds = 60;
        String avatarKey = CosFolderPath.AVATAR + "uid_" + uid + "_avatar.png";
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.PUT_OBJECT, List.of(avatarKey)
        );
        log.info("用户 {} 请求了头像修改权限", uid);
        return CosAuthorizationVO.keyAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), avatarKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setAvatarCustomization(Integer uid) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("avatar", "uid_" + uid + "_avatar.png");
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 不再使用默认头像", uid);
    }

    public GetDetailsVO getDetailsByUid(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        return new GetDetailsVO(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setNickname(Integer uid, String newNickname) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("nickname", newNickname);
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 修改了昵称: {}", uid, newNickname);
    }

    public void getEmailVerifyCodeForSettingEmail(String newEmail, String captchaUUID, String captchaCode) {
        if (!captchaService.checkCaptcha(captchaUUID, captchaCode)) {
            throw new ForumVerifyException("图形验证码错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", newEmail);
        if (userMapper.exists(queryWrapper)) {
            throw new ForumIllegalArgumentException("该邮箱已绑定其它账号");
        }
        emailService.sendVerifycode("更换绑定邮箱", newEmail);
        log.info("用户 {} 请求了邮箱验证码以修改邮箱: {}", getCurrentUser().getUid(), newEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setEmail(Integer uid, String newEmail, String verifyCode) {
        if (!emailService.judgeVerifyCode(newEmail, verifyCode)) {
            throw new ForumVerifyException("邮箱验证码错误");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("email", newEmail);
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 修改了邮箱: {}", uid, newEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setPassword(Integer uid, String oldPassword, String newPassword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ForumVerifyException("旧密码错误");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ForumVerifyException("新密码不得与旧密码一致");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("password", passwordEncoder.encode(newPassword));
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 修改了密码", uid);
    }

    public User getUserByUid(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return userMapper.selectOne(queryWrapper);
    }

    public Integer getCoinsByUid(Integer uid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getCoins);
        queryWrapper.eq(User::getUid, uid);
        return userMapper.selectOne(queryWrapper).getCoins();
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