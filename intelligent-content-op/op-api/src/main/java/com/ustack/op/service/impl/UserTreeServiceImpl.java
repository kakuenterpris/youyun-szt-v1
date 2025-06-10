package com.ustack.op.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.login.dto.BusDepInfoDTO;
import com.ustack.op.entity.BusDepInfoEntity;
import com.ustack.op.entity.BusSubCompanyInfoEntity;
import com.ustack.op.entity.BusUserInfoEntity;
import com.ustack.op.repo.BusDepInfoRepo;
import com.ustack.op.repo.BusSubCompanyInfoRepo;
import com.ustack.op.repo.BusUserInfoRepo;
import com.ustack.op.service.UserTreeService;
import com.ustack.resource.dto.UserTreeDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Liyingzheng
 * @data 2025/4/23 11:10
 * @describe
 */
@Service
@RequiredArgsConstructor
public class UserTreeServiceImpl implements UserTreeService {
    private final BusSubCompanyInfoRepo busSubCompanyInfoRepo;
    private final BusDepInfoRepo busDepInfoRepo;
    private final BusUserInfoRepo busUserInfoRepo;

    @Override
    public RestResponse get(String searchStr, String type, String parentId) {
        List<UserTreeDTO> resultList = new ArrayList<>();
        SystemUser currentUser = ContextUtil.currentUser();

        // TF本部id为5
        String subCompanyId = "5";
        // 查询当前登录人所在的公司信息
        BusDepInfoDTO busDepInfoDTO = busDepInfoRepo.getByDepNum(currentUser.getDepNum());
        if (Objects.nonNull(busDepInfoDTO)) {
            // 将公司id设置为当前登陆人所在公司
            subCompanyId = busDepInfoDTO.getSubCompanyId();
        }

        // 查询TF本部
        LambdaQueryWrapper<BusSubCompanyInfoEntity> busSubCompanyWrapper = new LambdaQueryWrapper<>();
        busSubCompanyWrapper.eq(BusSubCompanyInfoEntity::getSubCompanyId, subCompanyId)
                .eq(BusSubCompanyInfoEntity::getIsDeleted, false);
        BusSubCompanyInfoEntity busSubCompany = busSubCompanyInfoRepo.getOne(busSubCompanyWrapper);

        // 查询TF本部下的部门
        LambdaQueryWrapper<BusDepInfoEntity> busDepWrapper = new LambdaQueryWrapper<>();
        busDepWrapper.eq(BusDepInfoEntity::getSubCompanyId, subCompanyId)
                .eq(BusDepInfoEntity::getIsDeleted, false);
        List<BusDepInfoEntity> busDepList = busDepInfoRepo.list(busDepWrapper);
        List<String> depNumList = busDepList.stream().map(BusDepInfoEntity::getDepNum).toList();
        // 一级部门
        Map<String, List<BusDepInfoEntity>> busDepMap = busDepList.stream()
                .collect(Collectors.groupingBy(BusDepInfoEntity::getDepNum));

        // 查询TF本部下的用户
        LambdaQueryWrapper<BusUserInfoEntity> busUserWrapper = new LambdaQueryWrapper<>();
        busUserWrapper.eq(BusUserInfoEntity::getIsDeleted, false);
        // 根据入参模糊查询用户的名称、部门名称
        if (StringUtils.isNotBlank(searchStr)) {
            busUserWrapper.and(x -> x.like(BusUserInfoEntity::getUserName, searchStr)
//                    .or().like(BusUserInfoEntity::getUserNum, searchStr)
                    .or().like(BusUserInfoEntity::getDepName, searchStr));
        }
        // type取值：1全部，2本部门
        // 若type值为本部门，增加本部门的条件
        if (StringUtils.isNotBlank(type) && type.equals("2")) {
            busUserWrapper.eq(BusUserInfoEntity::getDepNum, currentUser.getDepNum());
        } else {
            // 否则，查询TF本部下所有部门的用户数据
            busUserWrapper.in(BusUserInfoEntity::getDepNum, depNumList);
        }
        List<BusUserInfoEntity> busUserList = busUserInfoRepo.list(busUserWrapper);
        Map<String, List<BusUserInfoEntity>> busUserMap = busUserList.stream().collect(Collectors.groupingBy(BusUserInfoEntity::getDepNum));

        // 将TF本部下的用户数据存入返回结果中
        if (!CollectionUtils.isEmpty(busUserMap)) {
            // type取值：1全部，2本部门
            // type值为空或为全部时，返回TF本部、TF本部下的部门、TF本部下的用户数据
            // type值为本部门时，返回本部门下的用户数据
            if (StringUtils.isBlank(type) || type.equals("1")) {
                // 将TF本部数据存入返回结果中
                if (busSubCompany != null) {
                    UserTreeDTO userTreeDTO = new UserTreeDTO();
                    userTreeDTO.setId(busSubCompany.getSubCompanyId());
                    userTreeDTO.setName(busSubCompany.getSubCompanyName());
                    userTreeDTO.setParentId(busSubCompany.getSupSubComId());
                    resultList.add(userTreeDTO);
                }
            }

            // 将用户和部门信息存入返回结果中
            busUserMap.forEach((key, value) -> {
                if (!CollectionUtils.isEmpty(value)) {
                    List<BusDepInfoEntity> depInfoEntities = busDepMap.get(key);
                    if ((StringUtils.isBlank(type) || type.equals("1"))
                            && !CollectionUtils.isEmpty(depInfoEntities)) {
                        depInfoEntities.forEach(item -> {
                            UserTreeDTO userTreeDTO = new UserTreeDTO();
                            // 部门为0时，为一级部门
                            if (item.getSupDepNum().equals("0")) {
                                userTreeDTO.setId(item.getDepNum());
                                userTreeDTO.setName(item.getDepName());
                                userTreeDTO.setParentId(item.getSubCompanyId());
                                resultList.add(userTreeDTO);
                            }
                        });
                    }
                    value.forEach(item -> {
                        UserTreeDTO userTreeDTO = new UserTreeDTO();
                        userTreeDTO.setId(item.getUserId());
                        userTreeDTO.setName(item.getUserName());
//                    userTreeDTO.setAvatar(currentUser.getAvatar());
                        List<BusDepInfoEntity> busDepInfoEntities = busDepMap.get(item.getDepNum());
                        if (!CollectionUtils.isEmpty(busDepInfoEntities)) {
                            busDepInfoEntities.forEach(depItem -> {
                                // 如果用户所在部门是一级部门，则设置为用户所在部门
                                if (depItem.getSupDepNum().equals("0")) {
                                    userTreeDTO.setParentId(item.getDepNum());
                                } else {
                                    // 如果用户所在部门不是一级部门，则设置父节点为用户所在部门的上级部门
                                    userTreeDTO.setParentId(depItem.getSupDepNum());
                                }
                            });
                        } else {
                            userTreeDTO.setParentId(busSubCompany.getSubCompanyId());
                        }
                        resultList.add(userTreeDTO);
                    });
                }
            });
        }

        return RestResponse.success(resultList, resultList.size());
    }
}
