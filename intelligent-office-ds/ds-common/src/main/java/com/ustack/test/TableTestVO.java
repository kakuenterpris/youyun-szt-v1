package com.ustack.test;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName table_test
 */
@Data
public class TableTestVO implements Serializable {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Integer age;


}