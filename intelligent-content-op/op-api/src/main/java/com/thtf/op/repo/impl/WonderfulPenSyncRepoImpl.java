package com.thtf.op.repo.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.emdedding.dto.WonderfulPenSyncDTO;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.BusResourceFolderEntity;
import com.thtf.op.mapper.BusResourceFolderMapper;
import com.thtf.op.repo.WonderfulPenSyncRepo;
import com.thtf.op.service.impl.KmServiceImpl;
import com.thtf.op.service.impl.TreeNodeServiceImpl;
import com.thtf.resource.dto.BusResourceManageListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class WonderfulPenSyncRepoImpl extends ServiceImpl<BusResourceFolderMapper, BusResourceFolderEntity>
        implements WonderfulPenSyncRepo {

    @Autowired
    private KmServiceImpl kmService;

    @Override
    public RestResponse pushFile(WonderfulPenSyncDTO dto) {


        return RestResponse.success("推送成功");
    }

    @Override
    public RestResponse getFileByUserId(WonderfulPenSyncDTO dto) {

        List<BusResourceManageListDTO> busResourceManageListDTOS = TreeNodeServiceImpl.assembleTree(kmService.getResourceListLeft("wonderfulPen", dto.getType()));
        return RestResponse.success(busResourceManageListDTOS);
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




