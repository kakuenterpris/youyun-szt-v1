package com.thtf.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.access.vo.UserInfoVO;
import com.thtf.chat.dto.AssignRolesDTO;
import com.thtf.chat.dto.UpdateUserInfoDto;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.entity.SysMenuEntity;
import com.thtf.chat.entity.SysUserRoleEntity;
import com.thtf.chat.manager.AsyncManager;
import com.thtf.chat.manager.factory.AsyncFactory;
import com.thtf.chat.mapper.BusUserInfoMapper;
import com.thtf.chat.mapper.SysUserRoleMapper;
import com.thtf.chat.repo.SysMenuRepo;
import com.thtf.chat.repo.SysUserRoleRepo;
import com.thtf.chat.service.BusUserInfoService;
import com.thtf.chat.util.BcryptUtil;
import com.thtf.chat.util.JwtUtil;
import com.thtf.chat.utils.MessageUtils;
import com.thtf.chat.utils.RedisUtil;
import com.thtf.constans.Constants;
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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;



@Service
public class BusUserInfoServiceImpl extends ServiceImpl<BusUserInfoMapper, BusUserInfoEntity>
    implements BusUserInfoService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final Integer SESSION_TIME_OUT = 86400000;

    @Autowired
    private SysMenuRepo sysMenuRepo;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserRoleRepo sysUserRoleRepo;


    @Override
    public RestResponse login(HttpServletRequest request, HttpServletResponse response, LoginDTO loginDTO) {
        // 验证码
        if (StringUtils.isNotEmpty(loginDTO.getUuid()) && StringUtils.isNotEmpty(loginDTO.getVerifyCode())){
            //return RestResponse.fail(1004, "请输入验证码！");
            Object o1 = redisUtil.get(loginDTO.getUuid());
            if (Objects.isNull(o1)){
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                return RestResponse.fail(1004, "验证码已过期！");
            }
            if (!StringUtils.equalsIgnoreCase(Objects.toString(o1), loginDTO.getVerifyCode())){
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                return RestResponse.fail(1004, "验证码错误！");
            }
        }
        BusUserInfoEntity user= this.getOne(new QueryWrapper<BusUserInfoEntity>().eq("login_id", loginDTO.getAccount()));
        if (null == user) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new CustomException(DefaultErrorCode.NAME_ERROR);
        }
        if (!BcryptUtil.match(loginDTO.getPassword(), user.getPassword())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_FAIL, MessageUtils.message("user.password.error")));
            throw new CustomException(DefaultErrorCode.USERNAME_PASSWORD_WRONG);
        }
        SystemUser userInfo = bs2User(user);
        TokenDTO token = getTokenDTO(request, response, userInfo);
        //todo  获取用户权限
        List<SysMenuEntity> userMenu = sysMenuRepo.getUserMenu(userInfo.getUserId());
//        流式构建子菜单
        List<SysMenuEntity> sysMenuEntities = userMenu.stream().filter(item -> Objects.equals(0L, item.getParentId())).map(item -> {
            item.setChildren(this.getChild(item.getMenuId(), userMenu));
            return item;
        }).toList();

        Gson gson = new Gson();
        try {
            boolean set = redisUtil.set("token_" + token.getToken(), user.toString(), 60 * 60 * 6);
            Cookie cookie = new Cookie("token", token.getToken());
            cookie.setMaxAge(SESSION_TIME_OUT);
            response.addCookie(cookie);
            String s = gson.toJson(sysMenuEntities);
            boolean setm = redisUtil.set("menu_" + token.getToken(), s, 60 * 60 * 6);
            System.out.println("set = " + set);
            System.out.println("setm = " + setm);
        }catch (Exception e){
            e.printStackTrace();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        return RestResponse.success(token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse addUser(UserInfoDto user) {
        try {
            BusUserInfoEntity busUserInfoEntity = dtoToEntity(user);
            String roleIds = user.getRoleIds();
            SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
            sysUserRoleEntity.setUserId(Long.valueOf(busUserInfoEntity.getUserId()));
            sysUserRoleEntity.setRoleId(Long.valueOf(roleIds));
            baseMapper.insert(busUserInfoEntity);
            sysUserRoleMapper.insert(sysUserRoleEntity);
        }catch (Exception e){
            return RestResponse.fail(1004, "添加失败！" + e.getMessage());
        }
        return RestResponse.success("添加成功");
    }

    @Override
    public Page<UserInfoVO> pageList(Page<UserInfoDto> pages, UserInfoVO vo) {
        Page<UserInfoVO> sysUserPage = baseMapper.selectPageByVO(pages,vo);
        return sysUserPage;
    }

    @Override
    public RestResponse updateByUserId(UpdateUserInfoDto user) {
        try {
            this.updateById(user);
            assignRoles(user);
        }catch (Exception e){
            return RestResponse.fail(1004, "修改失败！" + e.getMessage());
        }
        return RestResponse.success("修改成功");
    }
    protected boolean assignRoles(UpdateUserInfoDto dto) {
        // 分配角色
        AssignRolesDTO ard = new AssignRolesDTO();
        ard.setRoleIds(dto.getRoleIds());
        ard.setUserIds(Collections.singletonList(dto.getId().longValue()));
        return sysUserRoleRepo.assignRoles(ard);
    }

    private BusUserInfoEntity dtoToEntity(UserInfoDto user) {
        BusUserInfoEntity entity = new BusUserInfoEntity();
        entity.setLoginId(user.getLoginId());
        entity.setPassword(BcryptUtil.encode(user.getPassword()));
        entity.setUserName(user.getUserName());
        entity.setPhone(user.getPhone());
        entity.setDepName(user.getDepName());
        entity.setDepNum(user.getDepNum());
        entity.setUserId(user.getUserId());
        entity.setSpecialAuth("UNIT_FILE_MANAGE");
        entity.setEmail(user.getEmail());
        return entity;
    }

    /**
     * 获取子节点
     */
    private List<SysMenuEntity> getChild(Long id, List<SysMenuEntity> sysMenuEntities) {
        // 遍历所有节点，将所有表单分类的父id与传过来的根节点的id比较
        List<SysMenuEntity> childList = sysMenuEntities.stream().filter(e -> Objects.equals(id, e.getParentId())).toList();
        if (childList.isEmpty()) {
            // 没有子节点，返回一个空 List（递归退出）
            return null;
        }
        // 递归
        return childList.stream().map(e -> {
            e.setChildren(this.getChild(e.getMenuId(), sysMenuEntities));
            return e;
        }).toList();
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


