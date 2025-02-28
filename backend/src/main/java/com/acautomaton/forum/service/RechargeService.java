package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Recharge;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.mapper.RechargeMapper;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RechargeService {
    RechargeMapper rechargeMapper;

    @Autowired
    public RechargeService(RechargeMapper rechargeMapper) {
        this.rechargeMapper = rechargeMapper;
    }

    public PageHelperVO<Recharge> getRechargeList(Integer uid, Integer pageNumber, Integer pageSize) {
        LambdaQueryWrapper<Recharge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Recharge::getUid,uid);
        queryWrapper.select(Recharge::getId, Recharge::getChannel, Recharge::getType, Recharge::getStatus, Recharge::getAmount,Recharge::getSubject,
                Recharge::getComment, Recharge::getUpdateTime);
        queryWrapper.orderByDesc(Recharge::getUpdateTime);
        return new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize < 40 ? pageSize : 40)
                        .doSelectPageInfo(() -> rechargeMapper.selectList(queryWrapper))
        );
    }

    public Recharge getRechargeById(Integer id, Integer uid) {
        LambdaQueryWrapper<Recharge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Recharge::getId,id);
        queryWrapper.select(Recharge::getId, Recharge::getUid, Recharge::getUuid, Recharge::getTradeId, Recharge::getChannel,
                Recharge::getType, Recharge::getStatus, Recharge::getAmount, Recharge::getSubject, Recharge::getComment,
                Recharge::getCreateTime, Recharge::getUpdateTime);
        Recharge recharge = rechargeMapper.selectOne(queryWrapper);
        if (recharge == null || !recharge.getUid().equals(uid)) {
            throw new ForumIllegalArgumentException("交易不存在");
        }
        return recharge;
    }
}
