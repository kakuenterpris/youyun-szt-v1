package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.access.dto.SysRuleTagDto;
import com.ustack.chat.entity.SysRuleExtractEntity;
import com.ustack.chat.entity.SysRuleTagEntity;
import com.ustack.chat.mapper.SysRuleTagMapper;
import com.ustack.chat.repo.SysRuleTagRepo;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author cheng
 * @description 针对表【SYS_RULE_Tag】的数据库操作Service实现
* @createDate 2025-05-15 17:58:23
*/
@Service
public class SysRuleTagRepoImpl extends ServiceImpl<SysRuleTagMapper, SysRuleTagEntity>
    implements SysRuleTagRepo {

    @Override
    public RestResponse pageList(Page<SysRuleTagEntity> page, SysRuleTagDto dto) {
        try {
            LambdaQueryWrapper<SysRuleTagEntity> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.eq(dto.getRuleExtractId() != null, SysRuleTagEntity::getRuleExtractId, dto.getRuleExtractId());
            return RestResponse.success(this.page(page, roleQuery));
        }catch (Exception e){
            return RestResponse.error("查询失败");
        }
    }

    @Override
    public RestResponse list(SysRuleTagDto dto) {
        try {
            LambdaQueryWrapper<SysRuleTagEntity> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.eq(dto.getRuleExtractId() != null, SysRuleTagEntity::getRuleExtractId, dto.getRuleExtractId());
            return RestResponse.success(this.list(roleQuery));
        } catch (Exception e) {
            return RestResponse.error("查询失败");
        }
    }

    public RestResponse updateRuleTag(SysRuleTagEntity entity) {
        try {
            entity.setUpdateTime(new Date());
            entity.setUpdateUserId(ContextUtil.getUserId());
            entity.setUpdateUser(ContextUtil.getUserName());
            updateById(entity);
            return RestResponse.success("修改规则标签成功");
        } catch (Exception e) {
            return RestResponse.error("修改规则标签失败");
        }
    }


    public RestResponse saveRuleTag(SysRuleTagEntity entity) {
        if (entity.getRuleExtractId() == null) {
            return RestResponse.error("规则提取ID不能为空");
        }
        if (entity.getTagName() == null) {
            return RestResponse.error("标签名称不能为空");
        }
        if (entity.getTagCode() == null) {
            return RestResponse.error("标签编码不能为空");
        }

        //通过规则提取ID查询全部标签
        LambdaQueryWrapper<SysRuleTagEntity> queryWrapper = Wrappers.lambdaQuery(SysRuleTagEntity.class);
        LambdaQueryWrapper<SysRuleTagEntity> eq = queryWrapper.eq(SysRuleTagEntity::getRuleExtractId, entity.getRuleExtractId());
        List<SysRuleTagEntity> list = this.list(eq);

        //判断是否存在相同名称的标签
        for (SysRuleTagEntity tag : list) {
            if (tag.getTagName().equals(entity.getTagName())) {
                return RestResponse.error("标签名称已存在");
            }
        }

        //判断是否存在相同编码的标签
        for (SysRuleTagEntity tag : list) {
            if (tag.getTagCode().equals(entity.getTagCode())) {
                return RestResponse.error("标签编码已存在");
            }
        }

        try {
            entity.setUpdateTime(new Date());
            entity.setUpdateUserId(ContextUtil.getUserId());
            entity.setUpdateUser(ContextUtil.getUserName());
            entity.setCreator(ContextUtil.getUserId());
            entity.setCreateTime(new Date());
            Integer i = this.baseMapper.selectMaxSort();
            entity.setSort(i + 1);
            save(entity);
            return RestResponse.success("创建提取规则成功");
        } catch (Exception e) {
            log.error("创建提取规则失败", e);
            return RestResponse.error("创建提取规则失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public RestResponse sortRuleTag(Long id, Boolean isUp) {
        // 根据 id 查询当前标签
        SysRuleTagEntity tag1 = this.getById(id);
        if (tag1 == null) {
            return RestResponse.error("标签不存在");
        }
        Integer currentSort = tag1.getSort();

        LambdaQueryWrapper<SysRuleTagEntity> queryWrapper;
        if (isUp) {
            if (currentSort <= 0) {
                // 已经是第一个标签，无需交换排序值
                return RestResponse.success("已经是第一个标签，无需交换排序值");
            }
            // 查询上一个标签
            queryWrapper = Wrappers.lambdaQuery(SysRuleTagEntity.class)
                    .lt(SysRuleTagEntity::getSort, currentSort)
                    .orderByDesc(SysRuleTagEntity::getSort)
                    .last("LIMIT 1");
        } else {
            // 查询最大排序值
            Integer maxSort = this.getBaseMapper().selectMaxSort();
            if (currentSort >= maxSort) {
                return RestResponse.success("已经是最后一个标签，无需交换排序值");
            }
            // 查询下一个标签
            queryWrapper = Wrappers.lambdaQuery(SysRuleTagEntity.class)
                    .gt(SysRuleTagEntity::getSort, currentSort)
                    .orderByAsc(SysRuleTagEntity::getSort)
                    .last("LIMIT 1");
        }

        SysRuleTagEntity tag2 = this.getOne(queryWrapper);
        if (tag2 != null) {
            try {
                // 交换排序值
                Integer tempSort = tag1.getSort();
                tag1.setSort(tag2.getSort());
                tag2.setSort(tempSort);

                // 更新标签信息
                this.updateById(tag1);
                this.updateById(tag2);
            } catch (Exception e) {
                // 记录异常日志
                throw new RuntimeException("标签排序交换失败", e);
            }
        }
        return RestResponse.success("标签排序交换成功");
    }

}




