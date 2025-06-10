package com.ustack.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.emdedding.dto.FileUploadRecordDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@Mapper
public interface FileUploadRecordMapper extends BaseMapper<FileUploadRecordDTO> {

    FileUploadRecordDTO getByFileId(String fileId);
}
