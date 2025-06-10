package com.ustack.op.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.SysOptLogEntity;
import com.ustack.op.repo.SysOptLogRepo;
import com.ustack.op.service.KmService;
import com.ustack.op.service.SysOptLogService;
import com.ustack.resource.dto.BusResourceManageListDTO;
import com.ustack.resource.dto.QueryDTO;
import com.ustack.resource.dto.SystemLogDTO;
import com.ustack.resource.enums.OperateTypeEnum;
import com.ustack.resource.enums.ResourceTypeEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Liyingzheng
 * @description 针对表【sys_opt_log(操作日志表)】的数据库操作Service实现
 * @createDate 2025-04-22 16:04:46
 */
@Service
@RequiredArgsConstructor
public class SysOptLogServiceImpl implements SysOptLogService {

    private final SysOptLogRepo sysOptLogRepo;
    private final KmService kmService;

    @Override
    public RestResponse get(SystemLogDTO dto) {
        // 查有权限查看的左侧文件夹
//        List<BusResourceManageListDTO> resourceListLeft = kmService.getResourceListLeft();
        // 向下递归查找所有子文件夹
        List<Integer> idList = new ArrayList<>();
        // 当前查看的文件是文件夹时才向下递归查找所有子文件夹
        if (ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(dto.getFileType())) {
//            List<BusResourceManageListDTO> childrenList = TreeNodeServiceImpl.getChildrenList(resourceListLeft, Math.toIntExact(dto.getResourceId()));
//            idList = Linq.select(childrenList, BusResourceManageListDTO::getId);
            idList.add(Math.toIntExact(dto.getResourceId()));
        }

        // 查有权限查看的右侧树
        QueryDTO query = new QueryDTO();
        query.setPageNum(dto.getStart());
        query.setPageSize(dto.getSize());

        LambdaQueryWrapper<SysOptLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(dto.getId())) {
            wrapper.eq(SysOptLogEntity::getId, dto.getId());
        }
        if (Objects.nonNull(dto.getResourceId())) {
            // 如果是文件夹，则查询文件夹和文件夹下的文件的操作日志
//            if (ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(dto.getFileType())) {
//                wrapper.and(x -> x.or().in(SysOptLogEntity::getResourceId, idList)
//                        .or().in(SysOptLogEntity::getParentId, idList));
//            } else {
                wrapper.eq(SysOptLogEntity::getResourceId, dto.getResourceId());
//            }
//            query.setParentId(Math.toIntExact(dto.getResourceId()));
        }
        if (Objects.nonNull(dto.getParentId())) {
            wrapper.eq(SysOptLogEntity::getParentId, dto.getParentId());
        }
        // 如果是文件，则查询文件操作日志
        if (ResourceTypeEnum.RESOURCE_FILE.getCode().equals(dto.getFileType())) {
            wrapper.eq(SysOptLogEntity::getFileType, dto.getFileType());
        }
        if (StringUtils.isNotBlank(dto.getOperateType())) {
            wrapper.eq(SysOptLogEntity::getOperateType, dto.getOperateType());
        }
        if (StringUtils.isNotBlank(dto.getCreateUser())) {
            wrapper.eq(SysOptLogEntity::getCreateUser, dto.getCreateUser());
        }
        if (StringUtils.isNotBlank(dto.getCreateUserId())) {
            wrapper.eq(SysOptLogEntity::getCreateUserId, dto.getCreateUserId());
        }
        if (Objects.nonNull(dto.getCreateTime())) {
            wrapper.eq(SysOptLogEntity::getCreateTime, dto.getCreateTime());
        }
        wrapper.eq(SysOptLogEntity::getDeleted, 0)
                .orderByDesc(SysOptLogEntity::getCreateTime);
        Page<SysOptLogEntity> page = sysOptLogRepo.page(new Page<>(dto.getStart(), dto.getSize()), wrapper);

        List<SysOptLogEntity> records = new ArrayList<>(page.getRecords());
        for (Integer id : idList) {
            query.setParentId(id);
            List<SysOptLogEntity> rightList = getRightList(query);
            records.addAll(rightList);
        }
        List<SysOptLogEntity> result = records.stream().sorted(Comparator.comparing(SysOptLogEntity::getCreateTime).reversed()).toList();
        page.setRecords(result);
        page.setTotal(result.size());

        return RestResponse.success(page);
    }

    private List<SysOptLogEntity> getRightList(QueryDTO query) {
        List<SysOptLogEntity> result = new ArrayList<>();
        RestResponse response = kmService.resourceListRight(query, false);
        if (response.isSuccess() && response.getTotal() != 0) {
            List<BusResourceManageListDTO> resourceListRight = (List<BusResourceManageListDTO>) response.getData();
            // 有权限的文件夹
            List<Long> resourceIdList1 = resourceListRight.stream()
                    .filter(x -> x.getId() != null && x.getResourceType() == 1)
                    .map(x-> Long.valueOf(x.getId()))
                    .toList();
            // 有权限的文件
            List<Long> resourceIdList2 = resourceListRight.stream()
                    .filter(x -> x.getId() != null && x.getResourceType() == 2)
                    .map(x-> Long.valueOf(x.getId()))
                    .toList();

            LambdaQueryWrapper<SysOptLogEntity> wrapper = new LambdaQueryWrapper<>();
            if (!resourceIdList1.isEmpty()) {
                wrapper.in(SysOptLogEntity::getResourceId, resourceIdList1);
            }
            wrapper.eq(SysOptLogEntity::getParentId, query.getParentId())
                    .eq(SysOptLogEntity::getFileType, 1)
                    .eq(SysOptLogEntity::getDeleted, 0)
                    .orderByDesc(SysOptLogEntity::getCreateTime);
            List<SysOptLogEntity> list1 = sysOptLogRepo.list(wrapper);

            wrapper.clear();
            List<SysOptLogEntity> list2 = new ArrayList<>();
            if (!resourceIdList2.isEmpty()) {
                wrapper.in(SysOptLogEntity::getResourceId, resourceIdList2);
                wrapper.eq(SysOptLogEntity::getParentId, query.getParentId())
                        .eq(SysOptLogEntity::getFileType, 2)
                        .eq(SysOptLogEntity::getDeleted, 0)
                        .orderByDesc(SysOptLogEntity::getCreateTime);
                list2 = sysOptLogRepo.list(wrapper);
            }

            result.addAll(list1);

            Optional<Date> maxDateOpt = list2.stream()
                    .filter(x -> OperateTypeEnum.MOVE.getName().equals(x.getOperateType()))
                    .map(SysOptLogEntity::getCreateTime)
                    .max(Comparator.naturalOrder());
            if (maxDateOpt.isPresent()) {
                Date date = maxDateOpt.get();
                List<SysOptLogEntity> notMoveLogList = list2.stream()
                        .filter(x -> x.getCreateTime().compareTo(date) >= 0)
                        .toList();
                // 不包含移动前的日志
                result.addAll(notMoveLogList);
            } else {
                result.addAll(list2);
            }

        }
        return result;
    }
}




