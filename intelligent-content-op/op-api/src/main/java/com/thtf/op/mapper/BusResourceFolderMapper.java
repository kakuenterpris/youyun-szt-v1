package com.thtf.op.mapper;

import com.thtf.op.entity.BusResourceFolderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_folder(文件夹表)】的数据库操作Mapper
* @createDate 2025-04-23 11:32:26
* @Entity com.thtf.op.entity.BusResourceFolderEntity
*/
@Mapper
public interface BusResourceFolderMapper extends BaseMapper<BusResourceFolderEntity> {

    @Select("select * from bus_resource_folder")
    List<BusResourceFolderEntity> listAllIncludeDeletedFolder();

    @Select("select * from bus_resource_folder where parent_id = ${parentId}")
    List<BusResourceFolderEntity> listByParentIdIncludeDeletedFolder(Integer parentId);

    //通过userId和type查询文件夹
    @Select("select * from bus_resource_folder where create_user_id = ${userId} and type = ${type}")
    List<BusResourceFolderEntity> listByUserIdAndTypeFolder(Integer userId, Integer type);
}




