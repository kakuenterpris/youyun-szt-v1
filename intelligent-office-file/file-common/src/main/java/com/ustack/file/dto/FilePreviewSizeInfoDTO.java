package com.ustack.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description : 文件预览图的详细信息
 * @Author : LinXin
 * @ClassName : FilePreviewSizeInfoDTO
 * @Date: 2021-03-24 16:17
 */
@Data
public class FilePreviewSizeInfoDTO {


    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("PrimarySize")
    private String primarySize;

    @JsonProperty("ThumbSize")
    private String thumbSize;

    /** 生成预览图名称*/
    public String genPrimaryName(String suffix){
        String suf = StringUtils.isNotBlank(suffix) ? "." + suffix :  ".png";
        return this.id + suf;
    }

    /** 生成预览缩略图名称*/
    public String genThumbnailName(String suffix){
        String suf = StringUtils.isNotBlank(suffix) ? "_thumb." + suffix :  "_thumb.png";
        return this.id + suf;
    }

}
