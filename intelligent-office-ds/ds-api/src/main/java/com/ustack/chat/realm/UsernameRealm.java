package com.ustack.chat.realm;

import com.ustack.chat.entity.BusUserInfoEntity;
import com.ustack.chat.util.JwtToken;
import com.ustack.chat.util.JwtUtil;
import com.ustack.global.common.cache.RedisUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class UsernameRealm extends AuthorizingRealm  {


    private final RedisUtil redisUtil;
    @Autowired
    private JwtUtil jwtUtil;


    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 获取token信息
        String token = (String) authenticationToken.getCredentials();
        // 校验token：未校验通过或者已过期
        if (!jwtUtil.verifyToken(token) || jwtUtil.isExpire(token)) {
            throw new AuthenticationException("token已失效，请重新登录");
        }
        // 用户信息
        BusUserInfoEntity user = (BusUserInfoEntity) redisUtil.get("token_" + token);
        if (null == user) {
            throw new UnknownAccountException("用户不存在");
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user, token, this.getName());
        return simpleAuthenticationInfo;

    }
}
