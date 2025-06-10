package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.emdedding.dto.PushFileDTO;
import com.ustack.emdedding.dto.WonderfulPenSyncDTO;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.BusResourceFolderEntity;

import java.util.List;

/**
 * @author allm
 * @description 针对表【bus_dep_info(部门信息表)】的数据库操作Service
 * @createDate 2025-02-28 17:04:44
 */
public interface WonderfulPenSyncRepo extends IService<BusResourceFolderEntity> {

    RestResponse pushFile(PushFileDTO pushFileDTO);

    RestResponse getFileByUserId(WonderfulPenSyncDTO dto);

    RestResponse getKonwledgeByUserId(WonderfulPenSyncDTO dto);

    RestResponse getFileInfo(WonderfulPenSyncDTO dto);

    RestResponse selectFileByIds(WonderfulPenSyncDTO dto);
}
