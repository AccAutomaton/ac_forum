package com.acautomaton.forum.service.async;

import com.acautomaton.forum.entity.BrowseRecord;
import com.acautomaton.forum.enumerate.BrowseRecordType;
import com.acautomaton.forum.mapper.BrowseRecordMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class BrowseRecordAsyncService {
    BrowseRecordMapper browseRecordMapper;

    @Autowired
    public BrowseRecordAsyncService(BrowseRecordMapper browseRecordMapper) {
        this.browseRecordMapper = browseRecordMapper;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void createBrowseRecord(Integer viewer, BrowseRecordType browseRecordType, Integer beViewdId) {
        LambdaUpdateWrapper<BrowseRecord> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(BrowseRecord::getViewer, viewer);
        lambdaUpdateWrapper.eq(BrowseRecord::getType, browseRecordType);
        lambdaUpdateWrapper.eq(BrowseRecord::getBeViewedId, beViewdId);
        lambdaUpdateWrapper.apply("TO_DAYS(time) = TO_DAYS(NOW())");
        if (browseRecordMapper.exists(lambdaUpdateWrapper)) {
            lambdaUpdateWrapper.set(BrowseRecord::getTime, new Date());
            browseRecordMapper.update(lambdaUpdateWrapper);
            log.info("[Async]更新浏览记录: 用户 {} 浏览{} {}", viewer, browseRecordType.getValue(), beViewdId);
        } else {
            BrowseRecord browseRecord = new BrowseRecord(
                    null, viewer, browseRecordType, beViewdId, new Date(), 0
            );
            browseRecordMapper.insert(browseRecord);
            log.info("[Async]添加浏览记录: 用户 {} 浏览{} {}", viewer, browseRecordType.getValue(), beViewdId);
        }
    }
}
