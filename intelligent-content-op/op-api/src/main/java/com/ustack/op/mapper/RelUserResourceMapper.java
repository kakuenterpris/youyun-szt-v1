package com.ustack.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.emdedding.dto.ResourceDTO;
import com.ustack.op.entity.RelUserResourceEntity;
import com.ustack.resource.dto.RelUserResourceDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Admin_14104
 * @description 针对表【rel_user_resource】的数据库操作Mapper
 * @createDate 2025-02-19 12:18:15
 * @Entity com.ustack.op.entity.RelUserResourceEntity
 */
public interface RelUserResourceMapper extends BaseMapper<RelUserResourceEntity> {
    /**
     * 获取向量化未完成的文件信息
     *
     * @return
     */
    List<RelUserResourceDTO> getIndexingList();

    List<ResourceDTO> selectUnCompleteIndexing();

    void updateDocumentId(@Param("documentId") String documentId, @Param("resourceId") Long resourceId, @Param("fileId") String fileId, @Param("indexStatus") String indexStatus, @Param("indexStatusName") String indexStatusName);

    void updateIndexStatus(@Param("resourceId") Long resourceId, @Param("fileId") String fileId, @Param("indexStatus") String indexStatus, @Param("indexStatusName") String indexStatusName);

    void updateProgress(@Param("resourceId") Long resourceId, @Param("fileId") String fileId, @Param("progress") BigDecimal progress);

    List<Map> selectFileIdListByUserId(@Param("userId") String userId);

    List<Map> selectFolderIdListByUserId(@Param("userId") String userId);

    List<Map> getDocumentIdListByFolderId(@Param("folderId") String folderId);

    void updateIndexStatusByFolderId(String folderId, String indexingStatus, String indexingStatusName);

    void updateIndexStatusByFileIds(@Param("fileIdList")List<String> fileIdList, @Param("indexingStatus")String indexingStatus, @Param("indexingStatusName")String indexingStatusName);

    //通过file_id获取document_id
    @Select("select document_id from rel_user_resource where file_id = #{fileId}")
    String getDocumentIdByFileId(String fileId);


    //通过document_id获取file_id
    @Select("select file_id from rel_user_resource where document_id = #{documentId}")
    String getFileIdByDocumentId(String documentId);
}




