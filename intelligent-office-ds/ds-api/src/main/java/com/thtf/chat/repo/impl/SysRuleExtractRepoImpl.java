package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.access.dto.SysRoleDto;
import com.thtf.access.dto.SysRuleExtractDto;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.entity.SysRuleExtractEntity;
import com.thtf.chat.mapper.SysRuleExtractMapper;
import com.thtf.chat.repo.SysRuleExtractRepo;
import com.thtf.global.common.rest.ContextUtil;
import com.thtf.global.common.rest.RestResponse;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author cheng
 * @description 针对表【SYS_RULE_EXTRACT(知识规则提取配置)】的数据库操作Service实现
* @createDate 2025-05-15 17:47:15
*/
@Service
public class SysRuleExtractRepoImpl extends ServiceImpl<SysRuleExtractMapper, SysRuleExtractEntity>
    implements SysRuleExtractRepo {

    @Override
    public RestResponse pageList(Page<SysRuleExtractEntity> page, SysRuleExtractDto dto) {
        try {
            LambdaQueryWrapper<SysRuleExtractEntity> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.eq(dto.getName() != null, SysRuleExtractEntity::getName, dto.getName());
            roleQuery.eq(dto.getCode() != null, SysRuleExtractEntity::getCode, dto.getCode());
            return RestResponse.success(this.page(page, roleQuery));
        }catch (Exception e){
            return RestResponse.error("查询失败");
        }
    }

    public RestResponse saveRuleExtract(SysRuleExtractEntity entity) {
        try {
            entity.setCreateTime(new Date());
            entity.setCreator(ContextUtil.getUserId());
            save(entity);
            return RestResponse.success("创建提取规则成功");
        } catch (Exception e) {
            log.error("创建提取规则失败", e);
            return RestResponse.error("创建提取规则失败");
        }
    }

}




