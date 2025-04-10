package com.thtf.login.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description: 同方泛微OA登录信息
 * @author：linxin
 * @ClassName: TfJobInfoDTO
 * @Date: 2023-01-31 13:58
 */
@Data
public class FwoaLoginInfoDTO {

    /**
     * 同方泛微OA登录名-对应loginId
     */
    @JsonProperty("userName")
    private String userName;

    /**
     * verify-没有实际用处
     */
    private String verify;
}
