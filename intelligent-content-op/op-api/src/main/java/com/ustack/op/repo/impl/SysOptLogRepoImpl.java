package com.ustack.op.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.op.entity.SysOptLogEntity;
import com.ustack.op.repo.SysOptLogRepo;
import com.ustack.op.mapper.SysOptLogMapper;
import com.ustack.resource.enums.OperateTypeEnum;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author Liyingzheng
* @description 针对表【sys_opt_log(操作日志表)】的数据库操作Service实现
* @createDate 2025-04-22 16:04:46
*/
@Service
public class SysOptLogRepoImpl extends ServiceImpl<SysOptLogMapper, SysOptLogEntity>
    implements SysOptLogRepo {

    @Override
    public boolean saveLog(String operateContent, Long resourceId, Long parentId, String operateType, Integer fileType) {
        SysOptLogEntity sysOptLogEntity = SysOptLogEntity.builder()
                .resourceId(resourceId)
                .parentId(parentId)
                .operateType(operateType)
                .operateContent(operateContent)
                .fileType(fileType)
                .build();
        return this.save(sysOptLogEntity);
    }

    @Override
    public boolean updateParentId(Long resourceId, Long originParentId, Long newParentId) {
        return lambdaUpdate().set(SysOptLogEntity::getParentId, newParentId)
                .eq(SysOptLogEntity::getParentId, originParentId)
                .eq(SysOptLogEntity::getResourceId, resourceId)
                .update(new SysOptLogEntity());
    }

    @Override
    public boolean saveLogByIdAndParentId(Long resourceId, Long originParentId, Long parentId) {
        List<SysOptLogEntity> copyList = new ArrayList<>();

        List<SysOptLogEntity> list = lambdaQuery().eq(SysOptLogEntity::getParentId, originParentId)
                .eq(SysOptLogEntity::getResourceId, resourceId)
                .list();

        List<SysOptLogEntity> logEntities = list.stream()
                .filter(x -> OperateTypeEnum.MOVE.getName().equals(x.getOperateType())).toList();
        Optional<Date> maxDateOpt = Optional.empty();
        if (!logEntities.isEmpty()) {
            maxDateOpt = logEntities.stream().map(SysOptLogEntity::getCreateTime)
                    .max(Comparator.naturalOrder());
        }
        List<SysOptLogEntity> notMoveLogList = new ArrayList<>();
        if (maxDateOpt.isPresent()) {
            Date date = maxDateOpt.get();
            notMoveLogList = list.stream()
                    .filter(x -> x.getCreateTime().compareTo(date) >= 0)
                    .toList();
        }

        if (!notMoveLogList.isEmpty()) {
            notMoveLogList.forEach(item -> {
                item.setId(null);
                item.setResourceId(originParentId);
                item.setParentId(parentId);
                item.setFileType(1);
            });
            copyList = notMoveLogList;
        } else {
            list.forEach(item -> {
                item.setId(null);
                item.setResourceId(originParentId);
                item.setParentId(parentId);
                item.setFileType(1);
            });
            copyList = list;
        }

        return saveBatch(copyList);
    }
}




