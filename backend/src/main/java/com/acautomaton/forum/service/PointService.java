package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.PointRecord;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.PointRecordStatus;
import com.acautomaton.forum.enumerate.PointRecordType;
import com.acautomaton.forum.enumerate.UserLevel;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.mapper.PointRecordMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.vo.point.GetPointVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class PointService {
    UserMapper userMapper;
    PointRecordMapper pointRecordMapper;

    @Autowired
    public PointService(UserMapper userMapper, PointRecordMapper pointRecordMapper) {
        this.userMapper = userMapper;
        this.pointRecordMapper = pointRecordMapper;
    }

    public GetPointVO getPoint(User user) {
        return new GetPointVO(user.getPoints(), UserLevel.getByPoint(user.getPoints()));
    }

    public PageHelperVO<PointRecord> getPointRecordListByUid(Integer uid, Integer pageNumber, Integer pageSize) {
        LambdaQueryWrapper<PointRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointRecord::getUid, uid);
        queryWrapper.select(PointRecord::getId, PointRecord::getType, PointRecord::getPointVolume, PointRecord::getPointBalance,
                PointRecord::getProject, PointRecord::getComment, PointRecord::getStatus, PointRecord::getUpdateTime);
        queryWrapper.orderByDesc(PointRecord::getUpdateTime);
        return new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize < 40 ? pageSize : 40)
                        .doSelectPageInfo(() -> pointRecordMapper.selectList(queryWrapper))
        );
    }

    public PointRecord getPointRecordById(Integer id, Integer uid) {
        PointRecord pointRecord = pointRecordMapper.selectById(id);
        if (pointRecord == null || !pointRecord.getUid().equals(uid)) {
            throw new ForumExistentialityException("经验记录不存在");
        }
        return pointRecord;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addPoint(Integer uid, PointRecordType pointRecordType, Integer pointVolume, String project, String comment) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getUid, uid);
        User user = userMapper.selectOne(userLambdaUpdateWrapper);
        if (user == null) {
            throw new ForumExistentialityException("用户不存在");
        }
        user.setPoints(user.getPoints() + pointVolume);
        userMapper.updateById(user);
        Date now = new Date();
        pointRecordMapper.insert(new PointRecord(
                null, uid, pointRecordType, pointVolume, user.getPoints(), project, comment, PointRecordStatus.SUCCESS,
                now, now, 0
        ));
        log.info("用户 {} 积分 +{} ({})", uid, pointVolume, project);
    }
}
