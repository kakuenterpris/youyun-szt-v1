package com.ustack.file.dto;

import lombok.Data;

import java.util.List;

/**
 * @author linxin
 * @Description : TODO
 * @ClassName : FileBatchQueryDTO
 * @Date: 2022-11-03 09:07
 */
@Data
public class FileBatchQueryDTO {


    private List<String> params;

    /** 批量查询*/

    private Integer type;

}
