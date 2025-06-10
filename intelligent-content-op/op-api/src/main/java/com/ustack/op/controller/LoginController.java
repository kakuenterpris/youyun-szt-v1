package com.ustack.op.controller;

import cn.hutool.core.util.IdUtil;
import com.ustack.login.dto.WeaverDTO;
import com.ustack.op.service.LoginService;
import com.ustack.op.util.VerifyCodeUtil;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.RSAUtil;
import com.ustack.login.dto.CaptchaDTO;
import com.ustack.login.dto.FwoaLoginInfoDTO;
import com.ustack.login.dto.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: LoginController
 * @Date: 2025-02-17 23:07
 */
@RestController
@RequestMapping("/api/v1/op/login")
@Slf4j
@RequiredArgsConstructor
public class LoginController {


    private final LoginService service;
    private final RedisUtil redisUtil;
    @Value("${integration.rsa.public-key}")
    private String rsaPublicKey;
    @Value("${integration.rsa.private-key}")
    private String rsaPrivateKey;

    /**
     * 同步OA用户信息
     * @param id
     * @return
     */
    @PostMapping("/syncUserInfo")
    public RestResponse syncUserInfo(@RequestParam String id) {
        return service.syncUserInfo(id);
    }
    /**
     * 同步OA部门信息
     * @param id
     * @return
     */
    @PostMapping("/syncDepInfo")
    public RestResponse syncDepInfo(@RequestParam String id) {
        return service.syncDepInfo(id);
    }
    /**
     * 同步OA分部信息
     * @param id
     * @return
     */
    @PostMapping("/syncSubCompanyInfo")
    public RestResponse syncSubCompanyInfo(@RequestParam String id) {
        return service.syncSubCompanyInfo(id);
    }

    /**
     * 加密OA的登陆账户名
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
     * 获取RsaKey
     * @return
     */
    @GetMapping("/getRsaKey")
    public RestResponse getRsaKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        Base64.Encoder encoder = Base64.getEncoder();
        return RestResponse.success("public: " + encoder.encodeToString(keyPair.getPublic().getEncoded()) + "; private: " + encoder.encodeToString(keyPair.getPrivate().getEncoded()));
    }

    @PostMapping("/encrypt")
    public RestResponse encrypt(@RequestBody FwoaLoginInfoDTO dto) throws Exception {
        return RestResponse.success(RSAUtil.encrypt(dto.getUserName(),  rsaPublicKey));
    }

    @PostMapping("/decrypt")
    public RestResponse decrypt(@RequestBody FwoaLoginInfoDTO dto) throws Exception {
        return RestResponse.success(RSAUtil.decrypt(dto.getUserName(), rsaPrivateKey));
    }
}
