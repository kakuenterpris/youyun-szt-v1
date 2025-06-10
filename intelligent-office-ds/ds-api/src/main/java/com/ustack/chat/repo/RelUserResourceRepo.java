package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.RelUserResourceEntity;
import com.ustack.resource.dto.RelUserResourceDTO;
import com.ustack.resource.enums.IndexingStatusEnum;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【rel_user_resource】的数据库操作Service
 * @createDate 2025-02-19 12:18:15
 */
public interface RelUserResourceRepo extends IService<RelUserResourceEntity> {

    boolean add(RelUserResourceDTO relUserResourceDTO);

    List<RelUserResourceEntity> listByUserId(String userId);

    List<RelUserResourceEntity> list(String userId);

    List<RelUserResourceEntity> oneByField(String fileId);

    RelUserResourceDTO getOneByFileId(String fileId);

    boolean deleteByFileId(String fileId);

    /**
     * 根据fileId更新有云documentId和batch
     */
    boolean updateInfoFromDify(String fileId, String documentId, String batch, IndexingStatusEnum indexingStatusEnum);

    /**
     * 更新向量化状态
     */
    boolean updateIndexStatus(String documentId, String indexingStatus, String indexingStatusName);

    /**
     * 获取向量化未完成的文件信息
     * @return
     */
    List<RelUserResourceDTO> getIndexingList();
}
