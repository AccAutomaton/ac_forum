package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.entity.Vip;
import com.acautomaton.forum.enumerate.VipType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.mapper.VipMapper;
import com.acautomaton.forum.service.util.AlipayService;
import com.acautomaton.forum.service.util.RedisService;
import com.acautomaton.forum.vo.vip.GetPriceVO;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.GoodsDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VipService {
    VipMapper vipMapper;
    UserMapper userMapper;
    RedisService redisService;
    AlipayService alipayService;

    @Autowired
    public VipService(VipMapper vipMapper, UserMapper userMapper, RedisService redisService, AlipayService alipayService) {
        this.vipMapper = vipMapper;
        this.userMapper = userMapper;
        this.redisService = redisService;
        this.alipayService = alipayService;
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
            return new GetPriceVO(
                    ownCoins, deduction, targetVip.getPrice() - deduction, targetVip.getPrice(),
                    addDays(new Date(), targetVip.getDays())
            );
        } else if (targetVip == vip.getVipType()) {
            return new GetPriceVO(
                    ownCoins, deduction, targetVip.getPrice() - deduction, targetVip.getPrice(),
                    addDays(vip.getExpirationTime(), targetVip.getDays())
            );
        } else if (targetVip.getIndex() > vip.getVipType().getIndex()) {
            int currentRemainderDays = getTimeDistance(new Date(), vip.getExpirationTime());
            double targetPrice = (targetVip.getPrice() - (vip.getVipType().getPrice() * (currentRemainderDays / (vip.getVipType().getDays() * 1.0))));
            targetPrice = targetPrice < 0 ? 0 : targetPrice;
            deduction = ownCoins < targetPrice ? ownCoins : (int) targetPrice;
            return new GetPriceVO(
                    ownCoins, deduction, (int) targetPrice - deduction, (int) targetPrice,
                    addDays(new Date(), targetVip.getDays())
            );
        }
        throw new ForumIllegalAccountException("不能降级会员");
    }

    public String buyVip(Integer uid, VipType vipType, Integer mode) throws AlipayApiException {
        // TODO: 校验是否已有订单
        GetPriceVO priceVO = getPriceForTargetVip(uid, vipType);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String price = switch (mode) {
            case 0 -> String.valueOf(priceVO.getPriceWithCoins() * 1.0 / 100);
            case 1 -> String.valueOf(priceVO.getPriceWithoutCoins() * 1.0 / 100);
            default -> throw new ForumIllegalArgumentException("参数非法");
        };
        String subject = "AC论坛VIP - " + vipType.getValue();
        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setGoodsName(vipType.getValue());
        goodsDetail.setPrice(price);
        log.info("用户 {} 发起购买 {} (RMB {}) 的订单请求: {}", uid, vipType.getValue(), price, uuid);
        // TODO: 存储订单 UUID 进数据库
        return alipayService.createOrder(uuid, price, subject, "/vip", List.of(goodsDetail));
    }

    // TODO: 校验是否已有订单

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
