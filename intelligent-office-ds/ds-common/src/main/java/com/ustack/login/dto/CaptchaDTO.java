package com.ustack.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 二维码
 * @author：linxin
 * @ClassName: Captcha
 * @Date: 2023-02-25 01:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaptchaDTO {

    private String uuid;

    private String base64;

}
