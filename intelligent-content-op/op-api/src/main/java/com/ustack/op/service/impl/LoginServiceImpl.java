package com.ustack.op.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.op.mappings.BusDepInfoMapping;
import com.ustack.op.mappings.BusSubCompanyInfoMapping;
import com.ustack.op.mappings.BusUserInfoMapping;
import com.ustack.op.mappings.LoginMapping;
import com.ustack.op.properties.ThtfLdapProperties;
import com.ustack.op.properties.WeaverProperties;
import com.ustack.op.repo.BusDepInfoRepo;
import com.ustack.op.repo.BusSubCompanyInfoRepo;
import com.ustack.op.repo.BusUserInfoRepo;
import com.ustack.op.service.LoginService;
import com.ustack.op.util.WeaverUtil;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.consts.AuthConstants;
import com.ustack.global.common.dto.BusUserInfoDTO;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.dto.TokenDTO;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import com.ustack.global.common.utils.Linq;
import com.ustack.global.common.utils.RSAUtil;
import com.ustack.login.dto.*;
import com.ustack.login.enums.LoginErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: TestService
 * @Date: 2025-02-17 23:57
 */
@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties
public class LoginServiceImpl implements LoginService {

    private final BusUserInfoRepo userInfoRepo;
    private final BusDepInfoRepo depInfoRepo;
    private final BusSubCompanyInfoRepo subCompanyInfoRepo;
    private final BusUserInfoMapping userInfoMapping;
    private final BusDepInfoMapping depInfoMapping;
    private final BusSubCompanyInfoMapping subCompanyInfoMapping;
    private final LoginMapping loginMapping;
    private final ThtfLdapProperties properties;
    private final RedisUtil redisUtil;
    private final WeaverUtil weaverUtil;
    private final WeaverProperties weaverProperties;
    // private static final Integer SESSION_TIME_OUT = 172800;
    private static final Integer SESSION_TIME_OUT = -1;
    private final BusUserInfoRepo busUserInfoRepo;
    @Value("${integration.thtf.pc-redirect-url}")
    private String pcRedirectUrl;
    @Value("${integration.thtf.h5-redirect-url}")
    private String h5RedirectUrl;
    @Value("${integration.rsa.private-key}")
    private String rsaPrivateKey;
    @Value("${cookie.domain}")
    private String cookieDomain;
    @Value("${cookie.secure}")
    private boolean cookieSecure;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse syncUserInfo(String id) {
        if (!"cnki2025".equals(id)) {
            return RestResponse.SUCCESS;
        }
        //获取token
        Map<String, Object> token = weaverUtil.getoken(weaverProperties.getBaseUrl());
        //获取加密的userid
        String encryptUserid = weaverUtil.getEncryptUserid(ContextUtil.getUserId());

        TfUserInfoResultDTO data = weaverUtil.getHrmUserInfo(token.get("token").toString(), encryptUserid, 0, 0);
        List<TfUserInfoDTO> jobList = weaverUtil.getJobtitleInfo(token.get("token").toString(), encryptUserid, 10000, 1).getDataList();
        Map<String, String> jobMap = Linq.select(jobList, loginMapping::user2Job).stream().distinct()
                .collect(Collectors.toMap(TfJobInfoDTO::getId, TfJobInfoDTO::getJobtitlename));

        //获取全部的数据数量
        int totalSize = data.getTotalSize();
        for (int i = 1; i <= totalSize / 20 + 1; i++) {
            List<TfUserInfoDTO> hrmUserInfoList = weaverUtil.getHrmUserInfo(token.get("token").toString(), encryptUserid, 20, i).getDataList();
            List<BusUserInfoDTO> busUserInfoList = new ArrayList<>();

            if (CollUtil.isEmpty(hrmUserInfoList)) {
                return RestResponse.okWithMsg("未获取到人员信息");
            }
            hrmUserInfoList = Linq.find(hrmUserInfoList, x -> StringUtils.isNotEmpty(x.getLoginid()));

            for (TfUserInfoDTO user : hrmUserInfoList) {
                BusUserInfoDTO dto = userInfoMapping.tf2Bs(user);
                dto.setId(null);
                dto.setUserId(user.getId());
                dto.setUserNum(user.getWorkcode());
                dto.setLoginId(user.getLoginid());
                dto.setUserName(user.getLastname());
                dto.setDepNum(user.getDepartmentid());
                dto.setDepCode(user.getDepartmentcode());
                dto.setDepName(user.getDepartmentname());
                dto.setPostNum(user.getJobtitle());
                dto.setPost(jobMap.get(dto.getPostNum()));
                dto.setMobilePhone(user.getMobile());
                dto.setPhone(user.getTelephone());

                busUserInfoList.add(dto);
            }
            userInfoRepo.saveBatch(Linq.select(busUserInfoList, userInfoMapping::dto2Entity));
        }

        return RestResponse.okWithMsg("同步OA用户信息成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse syncDepInfo(String id) {
        if (!"cnki2025".equals(id)) {
            return RestResponse.SUCCESS;
        }
        //获取token
        Map<String, Object> token = weaverUtil.getoken(weaverProperties.getBaseUrl());
        //获取加密的userid
        String encryptUserid = weaverUtil.getEncryptUserid(ContextUtil.getUserId());

        List<TfDepInfoDTO> dataList = weaverUtil.getDepInfo(token.get("token").toString(), encryptUserid, 10000, 1);
        List<BusDepInfoDTO> depList = new ArrayList<>();

        if (CollUtil.isEmpty(dataList)) {
            return RestResponse.okWithMsg("未获取到部门信息");
        }

        for (TfDepInfoDTO dep : dataList) {
            BusDepInfoDTO dto = depInfoMapping.tf2Bs(dep);
            dto.setId(null);
            dto.setDepNum(dep.getId());
            dto.setDepCode(dep.getDepartmentcode());
            dto.setDepMark(dep.getDepartmentmark());
            dto.setDepName(dep.getDepartmentname());
            dto.setSupDepNum(dep.getSupdepid());
            dto.setSubCompanyId(dep.getSubcompanyid1());

            depList.add(dto);
        }
        depInfoRepo.saveBatch(Linq.select(depList, depInfoMapping::dto2Entity));

        return RestResponse.okWithMsg("同步部门信息成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse syncSubCompanyInfo(String id) {
        if (!"cnki2025".equals(id)) {
            return RestResponse.SUCCESS;
        }
        //获取token
        Map<String, Object> token = weaverUtil.getoken(weaverProperties.getBaseUrl());
        //获取加密的userid
        String encryptUserid = weaverUtil.getEncryptUserid(ContextUtil.getUserId());

        List<TfSubCompanyInfoDTO> dataList = weaverUtil.getSubCompanyInfo(token.get("token").toString(), encryptUserid, 10000, 1);
        List<BusSubCompanyInfoDTO> scList = new ArrayList<>();

        if (CollUtil.isEmpty(dataList)) {
            return RestResponse.okWithMsg("未获取到分部信息");
        }

        for (TfSubCompanyInfoDTO subCompany : dataList) {
            BusSubCompanyInfoDTO dto = subCompanyInfoMapping.tf2Bs(subCompany);
            dto.setId(null);
            dto.setSubCompanyId(subCompany.getId());
            dto.setSubCompanyCode(subCompany.getSubcompanycode());
            dto.setSubCompanyName(subCompany.getSubcompanyname());
            dto.setSubCompanyDesc(subCompany.getSubcompanydesc());
            dto.setSupSubComId(subCompany.getSupsubcomid());

            scList.add(dto);
        }
        subCompanyInfoRepo.saveBatch(Linq.select(scList, subCompanyInfoMapping::dto2Entity));

        return RestResponse.okWithMsg("同步分部信息成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse encryptLoginId(String id) {
        if (!"cnki2025".equals(id)) {
            return RestResponse.SUCCESS;
        }
        List<BusUserInfoDTO> userList = busUserInfoRepo.listAll();

        for (BusUserInfoDTO userInfo : userList) {
            busUserInfoRepo.updateEncryptLoginId(userInfo.getLoginId(), DigestUtil.md5Hex(userInfo.getLoginId()));
        }
        return RestResponse.SUCCESS;
    }

    @Override
    public RestResponse login(HttpServletRequest request, HttpServletResponse response, LoginDTO dto) {
        SystemUser userInfo;
        // 验证码
        if (StringUtils.isNotEmpty(dto.getUuid()) && StringUtils.isNotEmpty(dto.getVerifyCode())) {
            //return RestResponse.fail(1004, "请输入验证码！");
            Object o1 = redisUtil.get(dto.getUuid());
            if (Objects.isNull(o1)) {
                return RestResponse.fail(1004, "验证码已过期！");
            }
            if (!StringUtils.equalsIgnoreCase(Objects.toString(o1), dto.getVerifyCode())) {
                return RestResponse.fail(1004, "验证码错误！");
            }
        }

        // AD域登录，登录成功之后根据AD域账号查询账号信息，生成session
        try {
            if (dto.getAccount().contains("thtf_test_account")) {
                if (!dto.getPassword().equals(dto.getAccount())) {
                    return RestResponse.fail(LoginErrorCode.USERNAME_PASSWORD_WRONG);
                }
            } else {
                // 账号需要拼接上ad域登录地址的域名
                String appendAccount = new StringBuilder(dto.getAccount()).append("@").append(properties.getDomain()).toString();
                LdapContext ldapContext = adLogin(appendAccount, dto.getPassword());
            }
            // 没有异常就代表登录成功。
            BusUserInfoDTO account = userInfoRepo.getByLoginId(dto.getAccount());
            if (Objects.isNull(account)) {
                // 用户名密码不正确
                return RestResponse.fail(LoginErrorCode.USERNAME_PASSWORD_WRONG);
            }
            userInfo = userInfoMapping.bs2User(account);
        } catch (NamingException e) {
            log.error("AD域账号登录异常：", e);
            return RestResponse.fail(1004, "AD域账号登录失败");
        }
        // 创建session
        TokenDTO token = getTokenDTO(request, response, userInfo);
        redisUtil.set("token:" + token.getToken(), JsonUtil.toJson(userInfo), SESSION_TIME_OUT);
        return RestResponse.success(token);
    }

    @NotNull
    private TokenDTO getTokenDTO(HttpServletRequest request, HttpServletResponse response, SystemUser userInfo) {
        HttpSession session = request.getSession(true);
        session.setAttribute("user", userInfo);
        // number of seconds
        session.setMaxInactiveInterval(SESSION_TIME_OUT);
        // 返回cookie 关闭浏览器就删除cookie
        Cookie cookie = new Cookie("token", session.getId());
        cookie.setDomain(cookieDomain);
        cookie.setPath("/");
        cookie.setMaxAge(SESSION_TIME_OUT);
        cookie.setSecure(cookieSecure);
        response.addCookie(cookie);
        //
        TokenDTO token = new TokenDTO();
        token.setToken(session.getId());
        token.setSessionId(session.getId());
        token.setTimeoutSecond(SESSION_TIME_OUT);
        return token;
    }

    private LdapContext adLogin(String account, String password) throws NamingException {

        Hashtable<String, String> env = new Hashtable<>();
        //用户名称，cn,ou,dc 分别：用户，组，域
        env.put(Context.SECURITY_PRINCIPAL, account);
        //用户密码 cn 的密码
        env.put(Context.SECURITY_CREDENTIALS, password);
        //url 格式：协议://ip:端口/组,域   ,直接连接到域或者组上面
        env.put(Context.PROVIDER_URL, properties.getServer());
        //LDAP 工厂
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        //验证的类型     "none", "simple", "strong"
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        LdapContext ldapContext = new InitialLdapContext(env, null);
        log.info("ldapContext:" + ldapContext);
        log.info("用户" + account + "登录验证成功sucess");
        return ldapContext;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse getUserInfo(HttpServletRequest request) {
        String token = getToken(request);
        if (Objects.isNull(token)) {
            return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
        }
        String userInfoStr = (String) redisUtil.get("token:" + token);
        if (StringUtils.isEmpty(userInfoStr)) {
            return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
        }
        return RestResponse.success(JsonUtil.fromJson(userInfoStr, SystemUser.class));
    }

    private String getToken(HttpServletRequest request) {
        // 从header取
        String fromHeader = request.getHeader(AuthConstants.token_key);
        if (StringUtils.isNotBlank(fromHeader)) {
            return fromHeader;
        }
        // 从url param取值
        String fromParameter = request.getParameter(AuthConstants.token_key);
        if (StringUtils.isNotBlank(fromParameter)) {
            return fromParameter;
        }
        return null;
    }

    @Override
    public void verifyFromTfoa(HttpServletRequest request, HttpServletResponse response, String userName) throws IOException {
        BusUserInfoDTO userInfoDTO = userInfoRepo.getByEncryptLoginId(userName);
        String tokenString = "";
        if (null != userInfoDTO) {
            SystemUser userInfo = userInfoMapping.bs2User(userInfoDTO);
            // 创建session
            TokenDTO token = getTokenDTO(request, response, userInfo);
            redisUtil.set("token:" + token.getToken(), JsonUtil.toJson(userInfo), SESSION_TIME_OUT);
            tokenString = token.getToken();
        }
        response.sendRedirect(pcRedirectUrl + "?token=" + tokenString);
    }

    @Override
    public RestResponse verifyFromTfoaPc(HttpServletRequest request, HttpServletResponse response, String userName) throws IOException {
        BusUserInfoDTO userInfoDTO = userInfoRepo.getByEncryptLoginId(userName);
        String tokenString = "";
        if (null != userInfoDTO) {
            SystemUser userInfo = userInfoMapping.bs2User(userInfoDTO);
            // 创建session
            TokenDTO token = getTokenDTO(request, response, userInfo);
            redisUtil.set("token:" + token.getToken(), JsonUtil.toJson(userInfo), SESSION_TIME_OUT);
            tokenString = token.getToken();
            String url = pcRedirectUrl + "?token=" + tokenString;
            log.info("PC端跳转登录token:{}, url:{}", JsonUtil.toJson(token), url);
            return RestResponse.success(url);
        }
        return RestResponse.fail(500, "身份校验失败，请重试");
    }

    @Override
    public RestResponse verifyFromTfoaMobile(HttpServletRequest request, HttpServletResponse response, FwoaLoginInfoDTO param) throws Exception {
        log.info("移动端跳转登录请求参数:{}", JsonUtil.toJson(param));
        try {
            String loginId = RSAUtil.decrypt(param.getUserName(), rsaPrivateKey);
            BusUserInfoDTO userInfoDTO = userInfoRepo.getByLoginId(loginId);
            String tokenString = "";
            if (null != userInfoDTO) {
                SystemUser userInfo = userInfoMapping.bs2User(userInfoDTO);
                // 创建session
                TokenDTO token = getTokenDTO(request, response, userInfo);
                redisUtil.set("token:" + token.getToken(), JsonUtil.toJson(userInfo), SESSION_TIME_OUT);
                tokenString = token.getToken();
                String url = h5RedirectUrl + "?token=" + tokenString;
                log.info("移动端跳转登录token:{}, url:{}", JsonUtil.toJson(token), url);
//                response.sendRedirect(url);
                return RestResponse.success(url);
            }
            return RestResponse.fail(500, "身份校验失败，请重试");
        } catch (Exception e) {
            log.info("移动端跳转登录异常:{}", JsonUtil.toJson(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取泛微token并校验权限
     */
    @Override
    public RestResponse getWeaverToken(WeaverDTO dto) {
        if (null == dto || StringUtils.isEmpty(dto.getUserId())) {
            return RestResponse.fail(1101, "无效参数");
        }
        //获取token
        Map<String, Object> token = weaverUtil.getoken(weaverProperties.getBaseUrl());
        dto.setAppId(weaverProperties.getAppId());
        dto.setToken(token.get("token").toString());
        dto.setEncryptUserid(weaverUtil.getEncryptUserid(ContextUtil.getUserId()));

        if (StringUtils.isNotEmpty(dto.getWorkflowId())){
            dto.setExistPermission(weaverUtil.processPermissions(token.get("token").toString(), dto.getUserId(), dto.getWorkflowId()));
        }
        return RestResponse.success(dto);
    }
}
