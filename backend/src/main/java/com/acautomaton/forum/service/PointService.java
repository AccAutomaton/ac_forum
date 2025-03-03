package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.PointRecord;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.UserLevel;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.mapper.PointRecordMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.vo.point.GetPointVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
