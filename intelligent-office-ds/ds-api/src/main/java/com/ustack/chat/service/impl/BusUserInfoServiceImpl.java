package com.ustack.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.ustack.access.dto.UserInfoDto;
import com.ustack.access.vo.UserInfoVO;
import com.ustack.chat.dto.AssignRolesDTO;
import com.ustack.chat.dto.UpdateUserInfoDto;
import com.ustack.chat.entity.BusUserInfoEntity;
import com.ustack.chat.entity.SysMenuEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.entity.SysUserRoleEntity;
import com.ustack.chat.manager.AsyncManager;
import com.ustack.chat.manager.factory.AsyncFactory;
import com.ustack.chat.mapper.BusUserInfoMapper;
import com.ustack.chat.mapper.SysUserRoleMapper;
import com.ustack.chat.repo.SysMenuRepo;
import com.ustack.chat.repo.SysRoleRepo;
import com.ustack.chat.repo.SysUserRoleRepo;
import com.ustack.chat.service.BusUserInfoService;
import com.ustack.chat.util.BcryptUtil;
import com.ustack.chat.util.JwtUtil;
import com.ustack.chat.utils.MessageUtils;
import com.ustack.chat.utils.RedisUtil;
import com.ustack.constans.Constants;
import com.ustack.global.common.dto.BusUserInfoDTO;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.dto.TokenDTO;
import com.ustack.global.common.exception.CustomException;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.login.dto.LoginDTO;
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
import java.util.stream.Collectors;


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

    @Autowired
    private SysRoleRepo sysRoleRepo;


    @Override
    public RestResponse login(HttpServletRequest request, HttpServletResponse response, LoginDTO loginDTO) {
        BusUserInfoEntity user = this.getOne(new QueryWrapper<BusUserInfoEntity>().eq("login_id", loginDTO.getAccount()));
        if (null == user) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new CustomException(DefaultErrorCode.NAME_ERROR);
        }

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

        if (!BcryptUtil.match(loginDTO.getPassword(), user.getPassword())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_FAIL, MessageUtils.message("user.password.error")));
            addLoginErrorCount(user.getId());
            throw new CustomException(DefaultErrorCode.USERNAME_PASSWORD_WRONG);
        }
        SystemUser userInfo = bs2User(user);
        TokenDTO token = getTokenDTO(request, response, userInfo);
        //todo  获取用户权限
        List<SysMenuEntity> userMenu = sysMenuRepo.getUserMenu(userInfo.getId());
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
            cookie.setPath("/");
            response.addCookie(cookie);
            String s = gson.toJson(sysMenuEntities);
            boolean setm = redisUtil.set("menu_" + token.getToken(), s, 60 * 60 * 6);
            System.out.println("set = " + set);
            System.out.println("setm = " + setm);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (user.getLocked()) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginDTO.getAccount(), Constants.LOGIN_FAIL, MessageUtils.message("user.blocked")));
            return RestResponse.fail(1004, "用户已锁定，请联系管理员解锁！");
        }

        //登陆成功重置失败次数
        resetLoginErrorCount(user.getId());
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
        Page<BusUserInfoEntity> sysUserPage = baseMapper.selectPageByVO(pages,vo);
        Page<UserInfoVO> userInfoVOPage = new Page<>();
        userInfoVOPage.setCurrent(sysUserPage.getCurrent());
        userInfoVOPage.setSize(sysUserPage.getSize());
        userInfoVOPage.setTotal(sysUserPage.getTotal());
        List<UserInfoVO> collect = sysUserPage.getRecords().stream()
                .map(this::entity2VO)
                .collect(Collectors.toList());
        userInfoVOPage.setRecords(collect);
//        转换为vo对象
        return userInfoVOPage;
    }

    public UserInfoVO entity2VO(BusUserInfoEntity param) {
        if ( param == null ) {
            return null;
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId( param.getId() );
        userInfoVO.setUserNum( param.getUserNum() );
        userInfoVO.setLoginId( param.getLoginId() );
        userInfoVO.setUserName( param.getUserName() );
        userInfoVO.setUserDepNum( param.getDepNum() );
        userInfoVO.setUserDepName( param.getDepName() );
        userInfoVO.setUserPhone( param.getMobilePhone() );
        userInfoVO.setUserEmail( param.getEmail() );
        userInfoVO.setLocked( param.getLocked() );
        userInfoVO.setSecretLevel(param.getSecretLevel().toString());
        userInfoVO.setRoleId( param.getRoleId()==null? "": param.getRoleId().toString() );
        return userInfoVO;
    }

    @Override
    public RestResponse updateByUserId(UpdateUserInfoDto user) {
        try {
            //获取用户角色
            SysRoleEntity roleByUserId = getRoleByUserId();
            if ((roleByUserId==null||!roleByUserId.getRoleKey().equals("security"))&&(user.getRoleIds() != null||user.getSecretLevel()!=null||user.getLocked()!=null)){
                //todo 返回错误信息
                return RestResponse.fail(602, "非安全管理员用户不能操作！");
            }
            this.updateById(user);
            assignRoles(user);
        }catch (Exception e){
            return RestResponse.fail(1004, "修改失败！" + e.getMessage());
        }
        return RestResponse.success("修改成功");
    }
//    获取用户角色
    public SysRoleEntity getRoleByUserId(){
        SystemUser currentUser = ContextUtil.currentUser();
        String id = currentUser.getId();
        SysUserRoleEntity sysUserRole = sysUserRoleRepo.getOne(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getUserId, id));
        return sysUserRole==null?null:sysRoleRepo.getById(sysUserRole.getRoleId());
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
        cookie.setPath("/");
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
        systemUser.id(param.getId().toString());
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

    @Override
    public RestResponse unlockUser(Integer userId, Boolean unlock) {
        try {
            // 解锁用户 根据userId获取用户信息，只取一个用户信息
            LambdaQueryWrapper<BusUserInfoEntity> busUserInfoQuery = new LambdaQueryWrapper<>();
            busUserInfoQuery.eq(BusUserInfoEntity::getId, userId);
            BusUserInfoEntity busUserInfoEntity = this.getOne(busUserInfoQuery);
            busUserInfoEntity.setLocked(!unlock);
            this.updateById(busUserInfoEntity);
            if (unlock) {
                return RestResponse.success("解锁用户成功");
            } else {
                return RestResponse.success("锁定用户成功");
            }
        } catch (Exception e) {
            if (unlock) {
                return RestResponse.success("解锁用户失败");
            } else {
                return RestResponse.success("锁定用户失败");
            }

        }
    }

    /**
     * 重置锁定次数
     */
    public RestResponse resetLoginErrorCount(Integer id) {
        try {
            LambdaQueryWrapper<BusUserInfoEntity> busUserInfoQuery = new LambdaQueryWrapper<>();
            busUserInfoQuery.eq(BusUserInfoEntity::getId, id);
            BusUserInfoEntity busUserInfoEntity = this.getOne(busUserInfoQuery);
            busUserInfoEntity.setLoginFailCount(0);
            this.updateById(busUserInfoEntity);
            return RestResponse.success("重置用户登陆错误次数成功");
        } catch (Exception e) {
            log.error("重置用户登陆错误次数失败", e);
            return RestResponse.error("重置用户登陆错误次数失败");
        }
    }

    /**
     * 增加用户登陆错误次数
     *
     * @param id
     * @return
     */
    public RestResponse addLoginErrorCount(Integer id) {
        try {
            // 增加用户登陆错误次数
            LambdaQueryWrapper<BusUserInfoEntity> busUserInfoQuery = new LambdaQueryWrapper<>();
            busUserInfoQuery.eq(BusUserInfoEntity::getId, id);
            BusUserInfoEntity busUserInfoEntity = this.getOne(busUserInfoQuery);
            busUserInfoEntity.setLoginFailCount(busUserInfoEntity.getLoginFailCount() == null ? 0 : busUserInfoEntity.getLoginFailCount() + 1);
            this.updateById(busUserInfoEntity);
            // 如果登陆错误次数大于3，锁定用户,次数从字典获取
            if (busUserInfoEntity.getLoginFailCount() >= 3) {
                busUserInfoEntity.setLocked(true);
                this.updateById(busUserInfoEntity);
                return RestResponse.error("用户登陆错误次数过多，账号已被锁定");
            }
            return RestResponse.success("增加用户登陆错误次数成功");
        } catch (Exception e) {
            return RestResponse.error("增加用户登陆错误次数失败");
        }
    }

}


