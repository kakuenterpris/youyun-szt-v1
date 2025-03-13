package com.thtf.chat.controller;

import cn.hutool.core.util.IdUtil;
import com.thtf.chat.service.LoginService;
import com.thtf.chat.util.VerifyCodeUtil;
import com.thtf.chat.utils.RedisUtil;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.login.dto.CaptchaDTO;
import com.thtf.login.dto.FwoaLoginInfoDTO;
import com.thtf.login.dto.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: LoginController
 * @Date: 2025-02-17 23:07
 */
@RestController
@RequestMapping("/api/v1/chat/login")
@Slf4j
@RequiredArgsConstructor
public class LoginController {


    private final LoginService service;
    private final RedisUtil redisUtil;

    /**
     * 同步同方OA用户信息
     * @param id
     * @return
     */
    @PostMapping("/syncUserInfo")
    public RestResponse syncUserInfo(@RequestParam String id) {
        return service.syncUserInfo(id);
    }
    /**
     * 同步同方OA部门信息
     * @param id
     * @return
     */
    @PostMapping("/syncDepInfo")
    public RestResponse syncDepInfo(@RequestParam String id) {
        return service.syncDepInfo(id);
    }

    /**
     * 加密同方OA的登陆账户名
     * @param id
     * @return
     */
    @PostMapping("/encryptLoginId")
    public RestResponse encryptLoginId(@RequestParam String id) {
        return service.encryptLoginId(id);
    }

    /**
     * 登录
     * @param request
     * @param response
     * @param param
     * @return
     */
    @PostMapping("")
    public RestResponse login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDTO param) {
        return service.login(request, response, param);
    }

    @GetMapping("/captcha")
    public RestResponse<?> captcha(){
        VerifyCodeUtil verifyCodeUtil = new VerifyCodeUtil();
        String key = IdUtil.simpleUUID();
        verifyCodeUtil.generateCode();
        String text = verifyCodeUtil.getText();
        // String code = producer.createText();
        log.info("key = {}, code = {}", key, text);
        BufferedImage image = verifyCodeUtil.getCodeImg();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            ImageIO.write(image, "jpg", outputStream);

            Base64.Encoder encoder = Base64.getEncoder();
            String str = "data:image/jpeg;base64,";
            // 图片 Base64
            String base64Img = str + encoder.encodeToString(outputStream.toByteArray());
            redisUtil.set(key, text, 180L);
            return RestResponse.success(CaptchaDTO.builder().base64(base64Img).uuid(key).build());
        } catch (IOException e) {
            log.error("生成验证码异常：", e);
            return RestResponse.error( "生成验证码异常！");
        }
    }

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    @GetMapping("/getUserInfo")
    public RestResponse getUserInfo(HttpServletRequest request) {
        return service.getUserInfo(request);
    }

    /**
     * 提供给同方泛微OA用于登录
     * @param response
     * @param param
     * @return
     */
    @PostMapping("/verifyIdentityFromTfoa")
    public RestResponse verifyIdentityFromTfoa(HttpServletRequest request, HttpServletResponse response, @RequestBody FwoaLoginInfoDTO param) throws IOException {
        return service.verifyIdentityFromTfoa(request, response, param);
    }
}
