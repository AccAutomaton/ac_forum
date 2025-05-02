package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.SignIn;
import com.acautomaton.forum.entity.Vip;
import com.acautomaton.forum.enumerate.PointRecordType;
import com.acautomaton.forum.mapper.SignInMapper;
import com.acautomaton.forum.mapper.VipMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SignInService {
    SignInMapper signInMapper;
    VipMapper vipMapper;
    PointService pointService;

    @Autowired
    public SignInService(PointService pointService, VipMapper vipMapper, SignInMapper signInMapper) {
        this.pointService = pointService;
        this.vipMapper = vipMapper;
        this.signInMapper = signInMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer signIn(Integer uid) {
        if (isSignedInToday(uid)) {
            return 0;
        }
        Date now = new Date();
        signInMapper.insert(new SignIn(null, uid, now));
        Vip vip = vipMapper.selectById(uid);
        pointService.addPoint(uid, PointRecordType.SIGN_IN_INCOME, vip.getVipType().getPointsPerDay(),
                "每日签到", "");
        return vip.getVipType().getPointsPerDay();
    }

    public List<SignIn> getSignInInfoOfCurrentMonth(Integer uid) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfMonth = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endOfMonth = calendar.getTime();

        LambdaQueryWrapper<SignIn> signInLambdaQueryWrapper = new LambdaQueryWrapper<>();
        signInLambdaQueryWrapper
                .select(SignIn::getId, SignIn::getTime)
                .eq(SignIn::getUid, uid)
                .between(SignIn::getTime, startOfMonth, endOfMonth)
                .orderByAsc(SignIn::getTime);
        return signInMapper.selectList(signInLambdaQueryWrapper);
    }

    public List<SignIn> getSignInInfoOfMonth(Integer uid, Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfMonth = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endOfMonth = calendar.getTime();

        LambdaQueryWrapper<SignIn> signInLambdaQueryWrapper = new LambdaQueryWrapper<>();
        signInLambdaQueryWrapper
                .select(SignIn::getId, SignIn::getTime)
                .eq(SignIn::getUid, uid)
                .between(SignIn::getTime, startOfMonth, endOfMonth)
                .orderByAsc(SignIn::getTime);
        return signInMapper.selectList(signInLambdaQueryWrapper);
    }

    private Boolean isSignedInToday(Integer uid) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endOfDay = calendar.getTime();

        LambdaQueryWrapper<SignIn> signInLambdaQueryWrapper = new LambdaQueryWrapper<>();
        signInLambdaQueryWrapper
                .eq(SignIn::getUid, uid)
                .between(SignIn::getTime, startOfDay, endOfDay);
        return signInMapper.selectCount(signInLambdaQueryWrapper) > 0;
    }
}
