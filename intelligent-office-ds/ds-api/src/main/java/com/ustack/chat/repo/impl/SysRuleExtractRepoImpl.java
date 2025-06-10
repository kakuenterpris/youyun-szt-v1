package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.access.dto.SysRuleExtractDto;
import com.ustack.chat.entity.BusResourceFileEntity;
import com.ustack.chat.entity.BusResourceFolderEntity;
import com.ustack.chat.entity.SysRuleExtractEntity;
import com.ustack.chat.mapper.BusResourceFileMapper;
import com.ustack.chat.mapper.BusResourceFolderMapper;
import com.ustack.chat.mapper.SysRuleExtractMapper;
import com.ustack.chat.repo.SysRuleExtractRepo;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author cheng
 * @description 针对表【SYS_RULE_EXTRACT(知识规则提取配置)】的数据库操作Service实现
 * @createDate 2025-05-15 17:47:15
 */
@Service
public class SysRuleExtractRepoImpl extends ServiceImpl<SysRuleExtractMapper, SysRuleExtractEntity>
        implements SysRuleExtractRepo {

    @Autowired
    private SysRuleExtractMapper sysRuleExtractMapper;
    @Autowired
    private BusResourceFileMapper busResourceFileMapper;
    @Autowired
    private BusResourceFolderMapper busResourceFolderMapper;

    @Override
    public RestResponse pageList(Page<SysRuleExtractEntity> page, SysRuleExtractDto dto) {
        try {
            LambdaQueryWrapper<SysRuleExtractEntity> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.eq(dto.getName() != null, SysRuleExtractEntity::getName, dto.getName());
            roleQuery.eq(dto.getCode() != null, SysRuleExtractEntity::getCode, dto.getCode());
            return RestResponse.success(this.page(page, roleQuery));
        } catch (Exception e) {
            return RestResponse.error("查询失败");
        }
    }

    @Override
    public RestResponse list(SysRuleExtractDto dto) {
        try {
            LambdaQueryWrapper<SysRuleExtractEntity> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.eq(dto.getName() != null, SysRuleExtractEntity::getName, dto.getName());
            roleQuery.eq(dto.getCode() != null, SysRuleExtractEntity::getCode, dto.getCode());
            List<SysRuleExtractEntity> list = this.list(roleQuery);
            for (SysRuleExtractEntity entity : list) {
                entity.setTagCount(sysRuleExtractMapper.countRuleTagByRuleExtractId(entity.getId()));
            }
            return RestResponse.success(list);
        } catch (Exception e) {
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


    public void setRuleExtract(SysRuleExtractDto dto) {
        List<String> fileIds = dto.getFileIds();
        if (!fileIds.isEmpty()) {
            LambdaUpdateWrapper<BusResourceFileEntity> wrapper = Wrappers.lambdaUpdate(BusResourceFileEntity.class);
            wrapper.set(BusResourceFileEntity::getEmbeddingConfigCode, dto.getId())
                    .set(BusResourceFileEntity::getEmbeddingConfigName, dto.getName())
                    .in(BusResourceFileEntity::getId, fileIds);
            busResourceFileMapper.update(wrapper);
        }

        List<String> folderIds = dto.getFolderIds();
        if (!folderIds.isEmpty()) {
            LambdaUpdateWrapper<BusResourceFolderEntity> wrapper = Wrappers.lambdaUpdate(BusResourceFolderEntity.class);
            wrapper.set(BusResourceFolderEntity::getEmbeddingConfigCode, dto.getId())
                    .set(BusResourceFolderEntity::getEmbeddingConfigName, dto.getName())
                    .in(BusResourceFolderEntity::getId, folderIds);
            busResourceFolderMapper.update(wrapper);
        }
    }

}




