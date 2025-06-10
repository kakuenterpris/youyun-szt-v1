package com.ustack.file.dto;

import lombok.Data;

import java.util.List;

/**
 * @author linxin
 * @Description : TODO
 * @ClassName : SliceUploadResponse
 * @Date: 2022-07-08 09:59
 */
@Data
public class SliceUploadResponse {

    /** 秒传时为true */
    private Boolean skip;

    /** 已上传的分片数组 */
    private List<Integer> uploaded;

    /** 当秒传的时候 直接返回文件信息 */
    private FileUploadRecordDTO file;
}
