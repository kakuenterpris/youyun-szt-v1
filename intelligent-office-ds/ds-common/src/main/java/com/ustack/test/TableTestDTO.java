package com.ustack.test;

import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName table_test
 */
@Data
public class TableTestDTO implements Serializable {

    private Long id;
    /**
     *
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "名称不能为空")
    private String name;

    /**
     *
     */
    @Min(value = 1, groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "年龄不能小于1")
    @Max(value = 150, groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "年龄不能大于150")
    private Integer age;


}