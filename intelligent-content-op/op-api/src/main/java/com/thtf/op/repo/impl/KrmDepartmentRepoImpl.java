package com.thtf.op.repo.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.KrmDepartmentEntity;
import com.thtf.op.mapper.KrmDepartmentMapper;
import com.thtf.op.repo.KrmDepartmentRepo;
import com.thtf.op.util.OKHttpUtils;
import com.thtf.resource.dto.KrmDepartmentDTO;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lenovo
 * @description 针对表【KRM_DEPARTMENT(数聚平台部门体系)】的数据库操作Service实现
 * @createDate 2025-05-27 17:57:55
 */
@Service
public class KrmDepartmentRepoImpl extends ServiceImpl<KrmDepartmentMapper, KrmDepartmentEntity>
        implements KrmDepartmentRepo {

    @Value("${krm.api.url}")
    private String krmUrl;

    @Autowired
    private OKHttpUtils okHttpUtil;

    @Autowired
    private KrmDepartmentMapper krmDepartmentMapper;


    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");


    @Override
    public List<KrmDepartmentEntity> getTreeList(KrmDepartmentDTO dto) {
        // 获取所有部门数据
        LambdaQueryWrapper<KrmDepartmentEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(KrmDepartmentEntity::getSysId, dto.getSysId());
        List<KrmDepartmentEntity> allDepartments = list(wrapper);
        // 构建树形结构
        return buildTree(allDepartments);
    }

    private List<KrmDepartmentEntity> buildTree(List<KrmDepartmentEntity> nodes) {
        Map<String, KrmDepartmentEntity> nodeMap = new HashMap<>();
        List<KrmDepartmentEntity> rootNodes = new ArrayList<>();

        // 第一次遍历：创建所有节点的映射并识别根节点
        for (KrmDepartmentEntity node : nodes) {
            // 添加空children列表初始化
            if (node.getChildren() == null) {
                node.setChildren(new ArrayList<>());
            }
            nodeMap.put(node.getId(), node);
            if (node.getPId() == null || node.getPId().isEmpty()) {
                rootNodes.add(node);
            }
        }

        // 第二次遍历：建立父子关系
        for (KrmDepartmentEntity node : nodes) {
            if (node.getPId() != null && !node.getPId().isEmpty()) {
                KrmDepartmentEntity parent = nodeMap.get(node.getPId());
                if (parent != null) {
                    // 检测循环引用
                    if (isCircularReference(parent, node.getId(), nodeMap)) {
                        log.warn("检测到循环引用: " + parent.getId() + " 不能作为" + node.getId() + " 的子节点");
                        continue;
                    }

                    // 添加时按orderNum排序（升序）
                    parent.getChildren().add(node);
                    parent.getChildren().sort(Comparator.comparingInt(
                            n -> n.getOrderNum() != null ? n.getOrderNum() : 0));
                } else {
                    log.warn("无效的父节点ID: " + node.getPId() + "，节点 " + node.getId() + " 将被置为根节点");
                    rootNodes.add(node);
                }
            }
        }

        return rootNodes;
    }

    // 新增循环引用检测方法
    private boolean isCircularReference(KrmDepartmentEntity parent, String childId, Map<String, KrmDepartmentEntity> nodeMap) {
        while (parent != null) {
            if (parent.getPId() != null && parent.getPId().equals(childId)) {
                return true;
            }
            parent = nodeMap.get(parent.getPId());
        }
        return false;
    }


    @Override
    public RestResponse syncKnowledgeType(KrmDepartmentDTO dto) {
        String sysId = dto.getSysId();
        Map<String, Object> params = new HashMap<>();
        if (sysId == null) {
            return null;
        }
        params.put("sysId", sysId);
        String url = krmUrl + "/getKnowledgeType";
        Gson gson = new Gson();
        String json = gson.toJson(params);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Map map = okHttpUtil.doPost(request);

        if (map != null) {
            if (map.get("success") == null || !(Boolean) map.get("success")) {
                return null;
            }
            String content = map.get("content").toString();
            List<KrmDepartmentEntity> krmDepartmentEntityList = JSONUtil.toList(content, KrmDepartmentEntity.class);
            if (!krmDepartmentEntityList.isEmpty()) {
                //删除全部数据
                LambdaQueryWrapper<KrmDepartmentEntity> wrapper = Wrappers.lambdaQuery();
                krmDepartmentMapper.delete(wrapper);
                List<KrmDepartmentEntity> krmDepartmentEntitys = new ArrayList<>();
                // 新增保存逻辑
                saveDepartmentTree(krmDepartmentEntityList, krmDepartmentEntitys);
                this.saveBatch(krmDepartmentEntitys);
            }
        }
        return RestResponse.success("同步成功");
    }

    //删除
    public void delete() {
        LambdaQueryWrapper<KrmDepartmentEntity> wrapper = Wrappers.lambdaQuery();
        krmDepartmentMapper.delete(wrapper);
    }

    public void saveDepartmentTree(List<KrmDepartmentEntity> treeNodes, List<KrmDepartmentEntity> krmDepartmentEntities) {
        if (treeNodes == null) {
            return;
        }

        for (KrmDepartmentEntity node : treeNodes) {
            krmDepartmentEntities.add(node);
            List<KrmDepartmentEntity> children = JSONUtil.toList(node.getNode(), KrmDepartmentEntity.class);
            node.setChildren(children);
            saveDepartmentTree(node.getChildren(), krmDepartmentEntities);
        }
    }


    @Override
    public int addKnowledgeType(String sysId, String name, String pId) {
        Map params = new HashMap();
        params.put("sysId", sysId);
        params.put("name", name);
        params.put("pId", pId);
        String url = krmUrl + "/addKnowledgeType";
        Gson gson = new Gson();
        String json = gson.toJson(params);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Map map = okHttpUtil.doPost(request);
        if (map != null) {
            Integer result = (Integer) map.get("data");
            if (result != null) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public int updateKnowledgeType(String sysId, String name, String pId, String id) {

        Map params = new HashMap();
        params.put("sysId", sysId);
        params.put("name", name);
        params.put("pId", pId);
        String url = krmUrl + "/updateKnowledgeType";
        Gson gson = new Gson();
        String json = gson.toJson(params);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Map map = okHttpUtil.doPost(request);
        if (map != null) {
            Integer result = (Integer) map.get("data");
            if (result != null) {
                return result;
            }
        }

        return 0;
    }

    @Override
    public int deleteKnowledgeType(String sysId, String id) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        String url = krmUrl + "/delKnowledgeType";

        Gson gson = new Gson();
        Map<String, Object> params = new HashMap<>();
        params.put("sysId", sysId);
        params.put("id", id);
        String json = gson.toJson(params);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = null;
            responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (map != null && map.get("data") != null) {
                return (Integer) map.get("data");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return 0;
    }
}


