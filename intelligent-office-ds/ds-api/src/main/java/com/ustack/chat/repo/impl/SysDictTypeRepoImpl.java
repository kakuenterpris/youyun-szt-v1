package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.SysDictDataEntity;
import com.ustack.chat.entity.SysDictTypeEntity;
import com.ustack.chat.mapper.SysDictDataMapper;
import com.ustack.chat.mapper.SysDictTypeMapper;
import com.ustack.chat.repo.SysDictTypeRepo;
import com.ustack.chat.utils.StringUtils;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;


/**
* @author Lenovo
* @description 针对表【sys_dict_type】的数据库操作Service实现
* @createDate 2025-04-17 14:43:43
*/
@Service
public class SysDictTypeRepoImpl extends ServiceImpl<SysDictTypeMapper, SysDictTypeEntity>
    implements SysDictTypeRepo {

    @Autowired
    private SysDictDataMapper sysDictDataMapper;


    @Override
    public Page<SysDictTypeEntity> pageList(Page<SysDictTypeEntity> page, SysDictTypeEntity dictType) {
        LambdaQueryWrapper<SysDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        return page(page, queryWrapper);
    }


    @Override
    public RestResponse selectDictTypeById(Long dictId) {
        SysDictTypeEntity dictType = getById(dictId);
        return RestResponse.success(dictType);
    }

    @Override
    public RestResponse deleteDictTypeByIds(Long[] dictIds) {

        for (Long dictId : dictIds) {
            SysDictTypeEntity dictType = getById(dictId);
            if (dictType == null) {
                return RestResponse.error("字典类型不存在");
            }
            if (dictType.getStatus().equals("1")) {
                return RestResponse.error("字典类型已启用，不能删除");
            }
            // 删除字典数据
            LambdaQueryWrapper<SysDictDataEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysDictDataEntity::getDictType, dictType.getDictType());
            int dataRow = sysDictDataMapper.delete(queryWrapper);
            if (dataRow <= 0) {
                return RestResponse.error("删除字典数据失败");
            }
            int row = baseMapper.deleteById(dictId);
            if (row <= 0) {
                return RestResponse.error("删除字典类型失败");
            }

        }

        return RestResponse.success("");
    }

    @Override
    public RestResponse insertDictType(SysDictTypeEntity dictType) {
        int row = baseMapper.insert(dictType);
        if (row <= 0) {
            return RestResponse.error("新增字典类型失败");
        }
        return RestResponse.success(row);
    }

    @Override
    public RestResponse updateDictType(SysDictTypeEntity dictType) {

        int row = baseMapper.updateById(dictType);
        if (row <= 0) {
            return RestResponse.error("修改字典类型失败");
        }
        return RestResponse.success(row);
    }

    @Override
    public Boolean checkDictTypeUnique(SysDictTypeEntity dictType) {
        Long id = StringUtils.isNull(dictType.getId()) ? -1L : dictType.getId();
        LambdaQueryWrapper<SysDictTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictTypeEntity::getDictType, dictType.getDictType());
        SysDictTypeEntity one = getOne(queryWrapper);
        if (StringUtils.isNotNull(one) && one.getId().longValue() != id.longValue()) {
            return false;
        }
        return true;
    }
}




