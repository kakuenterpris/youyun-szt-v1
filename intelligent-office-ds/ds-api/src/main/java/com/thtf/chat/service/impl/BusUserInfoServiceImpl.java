package com.thtf.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.mapper.BusUserInfoMapper;
import com.thtf.chat.service.BusUserInfoService;
import com.thtf.chat.util.BcryptUtil;
import com.thtf.chat.util.JwtUtil;
import com.thtf.chat.utils.RedisUtil;
import com.thtf.global.common.dto.BusUserInfoDTO;
import com.thtf.global.common.dto.SystemUser;
import com.thtf.global.common.dto.TokenDTO;
import com.thtf.global.common.exception.CustomException;
import com.thtf.global.common.rest.DefaultErrorCode;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.login.dto.LoginDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BusUserInfoServiceImpl extends ServiceImpl<BusUserInfoMapper, BusUserInfoEntity>
    implements BusUserInfoService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final Integer SESSION_TIME_OUT = 86400000;



    @Override
    public RestResponse login(HttpServletRequest request, HttpServletResponse response, LoginDTO loginDTO) {
        BusUserInfoEntity user= this.getOne(new QueryWrapper<BusUserInfoEntity>().eq("login_id", loginDTO.getAccount()));
        if (null == user) {
            throw new CustomException(DefaultErrorCode.NAME_ERROR);
        }
        if (!BcryptUtil.match(loginDTO.getPassword(), user.getPassword())) {
            throw new CustomException(DefaultErrorCode.USERNAME_PASSWORD_WRONG);
        }
        SystemUser userInfo = bs2User(user);
        TokenDTO token = getTokenDTO(request, response, userInfo);
        try {
            boolean set = redisUtil.set("token_" + token.getToken(), user.toString(), 60 * 60 * 6);
            System.out.println("set = " + set);
        }catch (Exception e){
            e.printStackTrace();
        }


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

    public SystemUser bs2User(BusUserInfoEntity param) {
        if ( param == null ) {
            return null;
        }

        SystemUser.SystemUserBuilder systemUser = SystemUser.builder();

        systemUser.userId( param.getUserId() );
        systemUser.userName( param.getUserName() );
        systemUser.loginId( param.getLoginId() );
        systemUser.userNum( param.getUserNum() );
        systemUser.post( param.getPost() );
        systemUser.postNum( param.getPostNum() );
        systemUser.depName( param.getDepName() );
        systemUser.depNum( param.getDepNum() );
        systemUser.mobilePhone( param.getMobilePhone() );
        systemUser.phone( param.getPhone() );
        systemUser.email( param.getEmail() );
        systemUser.specialAuth( param.getSpecialAuth() );

        return systemUser.build();
    }

}


