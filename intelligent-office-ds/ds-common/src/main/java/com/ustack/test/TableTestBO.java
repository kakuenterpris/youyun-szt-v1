package com.ustack.test;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName table_test
 */
@Data
public class TableTestBO implements Serializable {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Integer age;


}