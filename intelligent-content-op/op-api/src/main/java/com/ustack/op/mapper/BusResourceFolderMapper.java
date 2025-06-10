package com.ustack.op.mapper;

import com.ustack.op.entity.BusResourceFolderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_folder(文件夹表)】的数据库操作Mapper
* @createDate 2025-04-23 11:32:26
* @Entity com.ustack.op.entity.BusResourceFolderEntity
*/
@Mapper
public interface BusResourceFolderMapper extends BaseMapper<BusResourceFolderEntity> {

    @Select("select * from bus_resource_folder")
    List<BusResourceFolderEntity> listAllIncludeDeletedFolder();

    @Select("select * from bus_resource_folder where parent_id = ${parentId}")
    List<BusResourceFolderEntity> listByParentIdIncludeDeletedFolder(Integer parentId);

    @Select("select * from bus_resource_folder where parent_id = ${parentId} and type = ${type}")
    List<BusResourceFolderEntity> listByParentIdAndTypeFolder(Integer parentId, Integer type);
}




