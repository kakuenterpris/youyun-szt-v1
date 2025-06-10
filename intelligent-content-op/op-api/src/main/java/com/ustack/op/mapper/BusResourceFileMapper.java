package com.ustack.op.mapper;

import com.ustack.op.entity.BusResourceFileEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.resource.dto.BusResourceManageListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_file(文件表)】的数据库操作Mapper
* @createDate 2025-04-23 11:43:03
* @Entity com.ustack.op.entity.BusResourceFileEntity
*/
@Mapper
public interface BusResourceFileMapper extends BaseMapper<BusResourceFileEntity> {
    List<BusResourceManageListDTO> selectListForLeft(String userId,List<Integer> fileIdList, String name, Integer parentId,
                                                     List<Integer> fileYearList, List<String> embeddingConfigNameList,
                                                     List<Integer> folderIdList, Boolean viewFile,
                                                     Integer begin, Integer end,String timeSort,String nameSort, Boolean notDelete);
    Integer selectCountForLeft(List<Integer> fileIdList,String name, Integer parentId,
                               List<Integer> fileYearList, List<String> embeddingConfigNameList,
                               List<Integer> folderIdList, Boolean viewFile, Boolean notDelete);


    List<BusResourceManageListDTO> selectFileList(String userId, String name, List<Integer> fileYearList,
                                                  List<String> embeddingConfigNameList, List<Integer> folderIdList,List<Integer> fileIdList,
                                                  String level, String embeddingStatus, String indexingStatus, Date startTime, Date endTime,
                                                  Integer begin, Integer end, String timeSort, String nameSort,
                                                  Boolean notDelete);
    Integer selectFileListCount( String name, List<Integer> fileYearList,
                                List<String> embeddingConfigNameList, List<Integer> folderIdList,List<Integer> fileIdList,
                                String level, String embeddingStatus, String indexingStatus, Date startTime, Date endTime,Boolean notDelete);

    //根据fileIds查询文件信息
    @Select("select file_id, name, level from bus_resource_file where file_id in ${fileIds}")
    List<BusResourceFileEntity> selectFileByIds(List<String> fileIds);

    //根据fileIds查询文件信息
    @Select("select level from bus_resource_file where file_id = #{fileId} and is_deleted = 0")
    Integer selectLevelByFileId(String fileId);

    @Select("select FILE_ID from bus_resource_file where folder_id = #{folderId} and is_deleted = 0")
    List<String> selectFileIdsByFolderId(Integer folderId);
}




