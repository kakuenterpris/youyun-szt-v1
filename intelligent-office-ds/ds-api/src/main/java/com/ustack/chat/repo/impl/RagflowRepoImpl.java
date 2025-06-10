package com.ustack.chat.repo.impl;

import com.ustack.chat.entity.RagflowEntity;
import com.ustack.chat.properties.RagflowConfigProperties;
import com.ustack.chat.repo.RagflowRepo;
import com.ustack.chat.utils.OKHttpUtils;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagflowRepoImpl implements RagflowRepo {

    @Autowired
    private RagflowConfigProperties ragflowConfigProperties;

    @Autowired
    private OKHttpUtils okHttpUtils;



    /**
     * 查看团队下所有用户
     * @return
     */
    @Override
    public RestResponse listUser() {
        String url = ragflowConfigProperties.getCommonUrl() + "/tenant/"+ContextUtil.getUserId()+"/user";
        Map map = okHttpUtils.doGet(url, null);
        if (map != null) {
            String code = map.get("code").toString();
            if ("200".equals(code)) {
                return RestResponse.success("查询成功");
            } else {
                return RestResponse.error(map.get("message").toString());
            }
        }
        return RestResponse.error("查询失败");
    }

    /**
     * 注册用户
     * @param ragflowEntity
     * @return
     */
    @Override
    public RestResponse registerUser(RagflowEntity ragflowEntity) {

        Map<String, String> params = new HashMap<>();
        String url = ragflowConfigProperties.getCommonUrl() + "/user/register";
        SystemUser systemUser = ContextUtil.currentUser();
        params.put("nickname", systemUser.getUserName());
        params.put("email", systemUser.getEmail());
        params.put("password", "");
        Map map = okHttpUtils.doPost(url, params);
        if (map != null) {
            String code = map.get("code").toString();
            if ("200".equals(code)) {
                return RestResponse.success("注册成功");
            } else {
                return RestResponse.error(map.get("message").toString());
            }
        }
        return RestResponse.error("注册失败");
    }

    /**
     * 邀请用户
     * @param ragflowEntity
     * @return
     */
    @Override
    public RestResponse inviteUser(RagflowEntity ragflowEntity) {
        Map<String, String> params = new HashMap<>();
        SystemUser systemUser = ContextUtil.currentUser();
        params.put("email", systemUser.getEmail());
        String url = ragflowConfigProperties.getCommonUrl() + "/tenant/"+systemUser.getUserId()+"/user";
        Map map = okHttpUtils.doPost(url, params);
        if (map != null) {
            String code = map.get("code").toString();
            if ("200".equals(code)) {
                return RestResponse.success("邀请用户成功");
            } else {
                return RestResponse.error(map.get("message").toString());
            }
        }
        return RestResponse.error("邀请用户失败");
    }

    @Override
    public RestResponse teams() {
        String url = ragflowConfigProperties.getCommonUrl() + "/tenant/list";
        Map map = okHttpUtils.doGet(url, null);
        if (map != null) {
            String code = map.get("code").toString();
            if ("200".equals(code)) {
                return RestResponse.success("查询成功");
            } else {
                return RestResponse.error(map.get("message").toString());
            }
        }
        return RestResponse.error("查询失败");
    }

    @Override
    public RestResponse accept(Map<String, String> params) {
        String url = ragflowConfigProperties.getCommonUrl() + "/tenant/agree/"+ContextUtil.getUserId();
        Map map = okHttpUtils.doPut(url, params);
        if (map != null) {
            String code = map.get("code").toString();
            if ("200".equals(code)) {
                return RestResponse.success("接受邀请成功");
            } else {
                return RestResponse.error(map.get("message").toString());
            }
        }
        return RestResponse.error("接受邀请失败");
    }

    @Override
    public RestResponse delete(String teamId) {
        String url = ragflowConfigProperties.getCommonUrl() + "/tenant/"+teamId+"/user/"+ContextUtil.getUserId();
        Map map = okHttpUtils.doDelete(url, null);
        if (map!= null) {
            String code = map.get("code").toString();
            if ("200".equals(code)) {
                return RestResponse.success("删除成功");
            } else {
                return RestResponse.error(map.get("message").toString());
            }
        }
        return RestResponse.error("删除失败");
    }


}
