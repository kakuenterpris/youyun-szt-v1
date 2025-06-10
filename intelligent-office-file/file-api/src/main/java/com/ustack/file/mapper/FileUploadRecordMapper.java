package com.ustack.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.file.entity.FileUploadRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @Description : 文件上传记录表Mapper
 * @Author : LinXin
 * @ClassName : FileUploadRecordMapper
 * @Date: 2021-03-10 13:07
 */
@Mapper
public interface FileUploadRecordMapper extends BaseMapper<FileUploadRecordEntity> {

    Long syncFileRecordByBusinessType(@Param("businessType") String businessType);
}
