package com.thtf.file.mappings;

import com.thtf.file.dto.FileUploadRecordDTO;
import com.thtf.file.entity.FileUploadRecordEntity;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: FileMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface FileMapping {

    FileUploadRecordEntity dto2Entity(FileUploadRecordDTO param);

    FileUploadRecordDTO entity2DTO(FileUploadRecordEntity param);

}
