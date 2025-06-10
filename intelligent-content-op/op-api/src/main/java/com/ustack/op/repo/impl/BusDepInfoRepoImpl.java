package com.ustack.op.repo.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.op.entity.BusDepInfoEntity;
import com.ustack.op.mapper.BusDepInfoMapper;
import com.ustack.op.mappings.BusDepInfoMapping;
import com.ustack.op.repo.BusDepInfoRepo;
import com.ustack.global.common.utils.Linq;
import com.ustack.login.dto.BusDepInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
* @author allm
* @description 针对表【bus_dep_info(部门信息表)】的数据库操作Service实现
* @createDate 2025-02-28 17:04:44
*/
@Service
@RequiredArgsConstructor
public class BusDepInfoRepoImpl extends ServiceImpl<BusDepInfoMapper, BusDepInfoEntity>
    implements BusDepInfoRepo {
    private final BusDepInfoMapping busDepInfoMapping;

    @Override
    public List<BusDepInfoDTO> listAll() {
        return Linq.select(lambdaQuery().eq(BusDepInfoEntity::getIsDeleted, false).list(), busDepInfoMapping::entity2Dto);
    }

    @Override
    public BusDepInfoDTO getByDepNum(String depNum) {
        BusDepInfoEntity entity = lambdaQuery()
                .eq(BusDepInfoEntity::getDepNum, depNum)
                .eq(BusDepInfoEntity::getIsDeleted, false)
                .one();
        return null == entity ? null : busDepInfoMapping.entity2Dto(entity);
    }

    @Override
    public List<BusDepInfoDTO> listAllSup(String depNum) {
        BusDepInfoDTO dep = this.getByDepNum(depNum);
        List<BusDepInfoDTO> depList = this.listAll();
        List<BusDepInfoDTO> supDepList = new ArrayList<>();
        getSup(dep.getSupDepNum(), supDepList, depList);
        return supDepList;
    }

    @Override
    public List<BusDepInfoDTO> listAllChild(String depNum) {
        BusDepInfoDTO dep = this.getByDepNum(depNum);
        List<BusDepInfoDTO> depList = this.listAll();
        List<BusDepInfoDTO> childList = new ArrayList<>();
        getChild(dep.getDepNum(), childList, depList);
        return childList;
    }

    /**
     * 根据节点封装树状图集合数据
     *
     * @param node      处理的节点(当前节点)
     * @param listNodes 要处理的列表数据
     */
    static <T extends BusDepInfoDTO> void getSup(String subDepNum, List<T> supDepList, List<T> listNodes) {
        if (subDepNum != null && !CollectionUtils.isEmpty(listNodes)) {
            // 循环节点数据，如果是子节点则添加起来
            T parent = Linq.first(listNodes, x -> x.getDepNum().equals(subDepNum));
            if (null != parent){
                supDepList.add(parent);
                getSup(parent.getSupDepNum(), supDepList, listNodes);
            }
        }
    }

    static <T extends BusDepInfoDTO> void getChild(String supDepNum, List<T> childList, List<T> listNodes) {
        if (!CollectionUtils.isEmpty(listNodes)) {
            List<T> allChild = Linq.find(listNodes, x -> supDepNum.equals(x.getSupDepNum()));
            if (CollUtil.isNotEmpty(allChild)){
                childList.addAll(allChild);
                for (T child : childList){
                    getChild(child.getDepNum(), childList, listNodes);
                }
            }
        }
    }
}




