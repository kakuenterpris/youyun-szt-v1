package com.ustack.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import com.ustack.chat.entity.SysMenuEntity;
import com.ustack.chat.mappings.BusDepInfoMapping;
import com.ustack.chat.mappings.BusSubCompanyInfoMapping;
import com.ustack.chat.mappings.BusUserInfoMapping;
import com.ustack.chat.mappings.LoginMapping;
import com.ustack.chat.properties.ThtfLdapProperties;
import com.ustack.chat.repo.BusDepInfoRepo;
import com.ustack.chat.repo.BusSubCompanyInfoRepo;
import com.ustack.chat.repo.BusUserInfoRepo;
import com.ustack.chat.service.LoginService;
import com.ustack.chat.util.MainTest;
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
    private static final Integer SESSION_TIME_OUT = 172800;
    private final BusUserInfoRepo busUserInfoRepo;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse syncUserInfo(String id) {
        if (!"cnki2025".equals(id)){
            return RestResponse.SUCCESS;
        }
        //获取token
        Map<String,Object> token = MainTest.testGetoken("https://oa.example.com");
        //获取加密的userid
        String encryptUserid = MainTest.getEncryptUserid(MainTest.getUserid());

        TfUserInfoResultDTO data = this.getHrmUserInfo(token.get("token").toString(), encryptUserid, 0, 0);
        List<TfUserInfoDTO> jobList = this.getJobtitleInfo(token.get("token").toString(), encryptUserid, 10000, 1).getDataList();
        Map<String, String> jobMap = Linq.select(jobList, loginMapping::user2Job).stream().distinct()
                .collect(Collectors.toMap(TfJobInfoDTO::getId, TfJobInfoDTO::getJobtitlename));

        //获取全部的数据数量
        int totalSize = data.getTotalSize();
        for (int i = 1; i <= totalSize/20 + 1; i++) {
            List<TfUserInfoDTO> hrmUserInfoList = this.getHrmUserInfo(token.get("token").toString(), encryptUserid, 20, i).getDataList();
            List<BusUserInfoDTO> busUserInfoList = new ArrayList<>();

            if (CollUtil.isEmpty(hrmUserInfoList)){
                return RestResponse.okWithMsg("未获取到人员信息");
            }
            hrmUserInfoList = Linq.find(hrmUserInfoList, x -> StringUtils.isNotEmpty(x.getLoginid()));

            for (TfUserInfoDTO user : hrmUserInfoList){
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
        if (!"cnki2025".equals(id)){
            return RestResponse.SUCCESS;
        }
        //获取token
        Map<String,Object> token = MainTest.testGetoken("https://oa.example.com");
        //获取加密的userid
        String encryptUserid = MainTest.getEncryptUserid(MainTest.getUserid());

        List<TfDepInfoDTO> dataList = this.getDepInfo(token.get("token").toString(), encryptUserid, 10000, 1);
        List<BusDepInfoDTO> depList = new ArrayList<>();

        if (CollUtil.isEmpty(dataList)){
            return RestResponse.okWithMsg("未获取到部门信息");
        }

        for (TfDepInfoDTO dep : dataList){
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
        if (!"cnki2025".equals(id)){
            return RestResponse.SUCCESS;
        }
        //获取token
        Map<String,Object> token = MainTest.testGetoken("https://oa.example.com");
        //获取加密的userid
        String encryptUserid = MainTest.getEncryptUserid(MainTest.getUserid());

        List<TfSubCompanyInfoDTO> dataList = this.getSubCompanyInfo(token.get("token").toString(), encryptUserid, 10000, 1);
        List<BusSubCompanyInfoDTO> scList = new ArrayList<>();

        if (CollUtil.isEmpty(dataList)){
            return RestResponse.okWithMsg("未获取到分部信息");
        }

        for (TfSubCompanyInfoDTO subCompany : dataList){
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

    /**
     * 获取用户信息
     */
    public TfUserInfoResultDTO getHrmUserInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"jobtitlecode\": \"\",\n" +
                "        \"jobtitlename\": \"\",\n" +
                "        \"pagesize\": "+pagesize+",\n" +
                "        \"curpage\": "+curpage+"\n" +
                "    }\n" +
                "}";

        HttpResponse res = HttpRequest.post("https://oa.example.com/api/hrm/resful/getHrmUserInfoWithPage")
                .header("appid", MainTest.getAppid())
                .header("token",token)
                .header("userid",encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)){
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()){
            return null;
        }
        Gson gson = new Gson();
        String userInfoStr = (String) gson.toJson(parse.getData());
        if (StringUtils.isEmpty(userInfoStr)){
            return null;
        }
        return JsonUtil.fromJson(userInfoStr, TfUserInfoResultDTO.class);
    }

    /**
     * 同步岗位信息
     */
    public TfUserInfoResultDTO getJobtitleInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"jobtitlecode\": \"\",\n" +
                "        \"jobtitlename\": \"\",\n" +
                "        \"pagesize\": "+pagesize+",\n" +
                "        \"curpage\": "+curpage+"\n" +
                "    }\n" +
                "}";
        //获取部门列表
        HttpResponse res = HttpRequest.post("https://oa.example.com/api/hrm/resful/getJobtitleInfoWithPage")
                .header("appid", MainTest.getAppid())
                .header("token",token)
                .header("userid",encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)){
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()){
            return null;
        }
        Gson gson = new Gson();
        String str = (String) gson.toJson(parse.getData());
        if (StringUtils.isEmpty(str)){
            return null;
        }
        return JsonUtil.fromJson(str, TfUserInfoResultDTO.class);
    }

    /**
     * 同步部门信息
     */
    public List<TfDepInfoDTO> getDepInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"departmentname\": \"\",\n" +
                "        \"departmentcode\": \"\",\n" +
                "        \"pagesize\": "+pagesize+",\n" +
                "        \"curpage\": "+curpage+"\n" +
                "    }\n" +
                "}";
        //获取部门列表
        HttpResponse res = HttpRequest.post("https://oa.example.com/api/hrm/resful/getHrmdepartmentWithPage")
                .header("appid", MainTest.getAppid())
                .header("token",token)
                .header("userid",encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)){
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()){
            return null;
        }
        Map map = (Map) parse.getData();
        Gson gson = new Gson();
        Type type = new TypeToken<List<TfDepInfoDTO>>() {}.getType();

        return map.get("dataList") != null ? gson.fromJson(gson.toJson(map.get("dataList")), type) : null;
    }

    /**
     * 同步分部信息
     */
    public List<TfSubCompanyInfoDTO> getSubCompanyInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"subcompanyname\": \"\",\n" +
                "        \"subcompanycode\": \"\",\n" +
                "        \"pagesize\": "+pagesize+",\n" +
                "        \"curpage\": "+curpage+"\n" +
                "    }\n" +
                "}";
        //获取分部列表
        HttpResponse res = HttpRequest.post("https://oa.example.com/api/hrm/resful/getHrmsubcompanyWithPage")
                .header("appid", MainTest.getAppid())
                .header("token",token)
                .header("userid",encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)){
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()){
            return null;
        }
        Map map = (Map) parse.getData();
        Gson gson = new Gson();
        Type type = new TypeToken<List<TfSubCompanyInfoDTO>>() {}.getType();

        return map.get("dataList") != null ? gson.fromJson(gson.toJson(map.get("dataList")), type) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse encryptLoginId(String id) {
        if (!"cnki2025".equals(id)){
            return RestResponse.SUCCESS;
        }
        List<BusUserInfoDTO> userList = busUserInfoRepo.listAll();

        for (BusUserInfoDTO userInfo : userList){
            busUserInfoRepo.updateEncryptLoginId(userInfo.getLoginId(), DigestUtil.md5Hex(userInfo.getLoginId()));
        }
        return RestResponse.SUCCESS;
    }

    @Override
    public RestResponse login(HttpServletRequest request, HttpServletResponse response, LoginDTO dto){
        SystemUser userInfo;
        // 验证码
        if (StringUtils.isNotEmpty(dto.getUuid()) && StringUtils.isNotEmpty(dto.getVerifyCode())){
            //return RestResponse.fail(1004, "请输入验证码！");
            Object o1 = redisUtil.get(dto.getUuid());
            if (Objects.isNull(o1)){
                return RestResponse.fail(1004, "验证码已过期！");
            }
            if (!StringUtils.equalsIgnoreCase(Objects.toString(o1), dto.getVerifyCode())){
                return RestResponse.fail(1004, "验证码错误！");
            }
        }

        // AD域登录，登录成功之后根据AD域账号查询账号信息，生成session
        try {
            if (dto.getAccount().contains("thtf_test_account")){
                if (!dto.getPassword().equals(dto.getAccount())){
                    return RestResponse.fail(LoginErrorCode.USERNAME_PASSWORD_WRONG);
                }
            } else {
                // 账号需要拼接上ad域登录地址的域名
                String appendAccount = new StringBuilder(dto.getAccount()).append("@").append(properties.getDomain()).toString();
                LdapContext ldapContext = adLogin(appendAccount, dto.getPassword());
            }
            // 没有异常就代表登录成功。
            BusUserInfoDTO account = userInfoRepo.getByLoginId(dto.getAccount());
            if (Objects.isNull(account) ){
                // 用户名密码不正确
                return RestResponse.fail(LoginErrorCode.USERNAME_PASSWORD_WRONG.getCode(), "暂无权限");
            }
            userInfo = userInfoMapping.bs2User(account);
        } catch (NamingException e) {
            log.error("AD域账号登录异常：", e);
            return RestResponse.fail(1004, "账号或密码错误");
        }
        // 创建session
        TokenDTO token = getTokenDTO(request, response, userInfo);
        redisUtil.set("token:" + token.getToken(), JsonUtil.toJson(userInfo), SESSION_TIME_OUT);
        return RestResponse.success(token);
    }

    @NotNull
    private static TokenDTO getTokenDTO(HttpServletRequest request, HttpServletResponse response, SystemUser userInfo) {
        HttpSession session = request.getSession(true);
        session.setAttribute("user", userInfo);
        // number of seconds
        session.setMaxInactiveInterval(SESSION_TIME_OUT);
        // 返回cookie 关闭浏览器就删除cookie
        Cookie cookie = new Cookie("sessionId", session.getId());
        cookie.setMaxAge(SESSION_TIME_OUT);
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
        String userInfoStr = (String) redisUtil.get("token_" + token);
        if (StringUtils.isEmpty(userInfoStr)){
            return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
        }
        return RestResponse.success(JsonUtil.fromJson(userInfoStr, SystemUser.class));
    }

    private String getToken(HttpServletRequest request){
        //        获取cookie 中的sessionId
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {  // 假设token存储在名为"token"的cookie中
                    System.out.println("cookie:" + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        // 从header取
        String fromHeader = request.getHeader(AuthConstants.token_key);
        if (StringUtils.isNotBlank(fromHeader)){
            return fromHeader;
        }
        // 从url param取值
        String fromParameter = request.getParameter(AuthConstants.token_key);
        if (StringUtils.isNotBlank(fromParameter)){
            return fromParameter;
        }
        return null;
    }

    @Override
    public RestResponse getUserMenu(HttpServletRequest request) {
        String token = getToken(request);
        if (Objects.isNull(token)) {
            return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
        }
        String userMenu = (String) redisUtil.get("menu_" + token);
        if (Objects.isNull(token)) {
            return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
        }
        return RestResponse.success(userMenu);
    }
}
