package com.ustack.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.op.entity.BusResourceDatasetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_dataset(人员或部门与知识库关联表)】的数据库操作Mapper
* @createDate 2025-03-27 17:43:25
* @Entity com.ustack.op.entity.BusResourceDatasetEntity
*/
@Mapper
public interface BusResourceDatasetMapper extends BaseMapper<BusResourceDatasetEntity> {

    //通过create_user_id查询folder_id
    List<String> listDatasetsIdByFolderIds(List<String> folderIds);


}




