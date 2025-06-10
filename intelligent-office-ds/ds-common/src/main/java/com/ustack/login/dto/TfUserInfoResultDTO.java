package com.ustack.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 用户信息
 * @author：linxin
 * @ClassName: UserInfoDTO
 * @Date: 2023-01-31 13:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TfUserInfoResultDTO {

    /**
     *
     */
    private Integer totalSize;

    /**
     *
     */
    private List<TfUserInfoDTO> dataList;
}
