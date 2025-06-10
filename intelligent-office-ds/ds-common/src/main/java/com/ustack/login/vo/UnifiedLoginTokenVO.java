package com.ustack.login.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 登录接口返回对象
 * @author：linxin
 * @ClassName: UnifiedLoginTokenVO
 * @Date: 2023-01-31 12:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnifiedLoginTokenVO {

    private String token;

    private Long timeoutMinutes;
}
