package com.thtf.op.repo.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.emdedding.dto.WonderfulPenSyncDTO;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.global.common.utils.Linq;
import com.thtf.login.dto.BusDepInfoDTO;
import com.thtf.op.entity.BusDepInfoEntity;
import com.thtf.op.mapper.BusDepInfoMapper;
import com.thtf.op.mappings.BusDepInfoMapping;
import com.thtf.op.repo.BusDepInfoRepo;
import com.thtf.op.repo.WonderfulPenSyncRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class WonderfulPenSyncRepoImpl implements WonderfulPenSyncRepo {

    @Override
    public RestResponse pushFile(WonderfulPenSyncDTO dto) {


        return RestResponse.success("推送成功");
    }

    @Override
    public RestResponse getFileByUserId(WonderfulPenSyncDTO dto) {
        return RestResponse.success("检索成功");
    }

    @Override
    public RestResponse getKonwledgeByUserId(WonderfulPenSyncDTO dto) {


        //{"userId":用户id
        // XXX：[{
        //  "type":1  --(1:个人、2：部门、3：企业知识库)
        //  "folderId": a,b,c
        // },
        // {
        //  "type":2  --(1:个人、2：部门、3：企业知识库)
        //  "folderId": e,r,t
        // }
        // ]
        //
        //"query":用户输入内容
        //
        //}


        JSONObject object = new JSONObject();
        object.put("userId", dto.getUserId());
        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 1);
        jsonObject.put("folderId", "a,b,c");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("type", 2);
        jsonObject1.put("folderId", "e,r,t");
        list.add(jsonObject);
        list.add(jsonObject1);
        object.put("XXX", list);
        object.put("query", dto.getQuery());
        return RestResponse.success(object);

    }

    @Override
    public RestResponse getFileInfo(WonderfulPenSyncDTO dto) {
        return RestResponse.success("预览成功");
    }
}




