package com.thtf.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.emdedding.dto.WonderfulPenSyncDTO;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.login.dto.BusDepInfoDTO;
import com.thtf.op.entity.BusDepInfoEntity;

import java.util.List;

/**
 * @author allm
 * @description 针对表【bus_dep_info(同方部门信息表)】的数据库操作Service
 * @createDate 2025-02-28 17:04:44
 */
public interface WonderfulPenSyncRepo {

    RestResponse pushFile(WonderfulPenSyncDTO dto);

    RestResponse getFileByUserId(WonderfulPenSyncDTO dto);

    RestResponse getKonwledgeByUserId(WonderfulPenSyncDTO dto);

    RestResponse getFileInfo(WonderfulPenSyncDTO dto);
}
