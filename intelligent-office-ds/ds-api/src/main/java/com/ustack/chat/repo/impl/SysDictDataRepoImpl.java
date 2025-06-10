package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.SysDictDataEntity;
import com.ustack.chat.mapper.SysDictDataMapper;
import com.ustack.chat.repo.SysDictDataRepo;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【sys_dict_data】的数据库操作Service实现
* @createDate 2025-04-17 15:56:51
*/
@Service
public class SysDictDataRepoImpl extends ServiceImpl<SysDictDataMapper, SysDictDataEntity>
    implements SysDictDataRepo {


    @Override
    public Page<SysDictDataEntity> pageList(Page<SysDictDataEntity> page,SysDictDataEntity dictDataEntity) {

        LambdaQueryWrapper<SysDictDataEntity> queryWrapper = new LambdaQueryWrapper<>();
        return page(page, queryWrapper);
    }


    @Override
    public RestResponse selectDictDataByType(String dictType) {
        if (dictType == null) {
            return RestResponse.error("字典类型不能为空");
        }
        List<SysDictDataEntity> list = lambdaQuery()
                .eq(SysDictDataEntity::getDictType, dictType)
                .eq(SysDictDataEntity::getStatus, "1").orderByAsc(SysDictDataEntity::getDictSort).list();
        if (list.isEmpty()) {
            return RestResponse.error("没有查询到数据");
        }
        List<Map<String, Object>> result = list.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dictValue", item.getDictValue());
            map.put("dictLabel", item.getDictLabel());
            return map;
        }).collect(Collectors.toList());
        return RestResponse.success(result,result.size());
    }

    @Override
    public RestResponse selectDictDataById(Long dictCode) {
        if (dictCode == null) {
            return RestResponse.error("字典编码不能为空");
        }
        SysDictDataEntity dictDataEntity = getById(dictCode);
        return dictDataEntity != null ? RestResponse.success(dictDataEntity) : RestResponse.error("没有查询到数据");
    }

    @Override
    public RestResponse insertDictData(SysDictDataEntity dictDataEntity) {
        int insert = baseMapper.insert(dictDataEntity);
        if (insert > 0) {
            List<SysDictDataEntity> list = lambdaQuery()
                    .eq(SysDictDataEntity::getDictType, dictDataEntity.getDictType())
                    .eq(SysDictDataEntity::getStatus, "1").orderByAsc(SysDictDataEntity::getDictSort).list();
            return RestResponse.success(list,list.size());
        }
        return RestResponse.error("新增失败");
    }

    @Override
    public RestResponse updateDictData(SysDictDataEntity dictDataEntity) {
        int update = baseMapper.updateById(dictDataEntity);
        if (update > 0) {
            List<SysDictDataEntity> list = lambdaQuery()
                    .eq(SysDictDataEntity::getDictType, dictDataEntity.getDictType())
                    .eq(SysDictDataEntity::getStatus, "1").orderByAsc(SysDictDataEntity::getDictSort).list();
            return RestResponse.success(list,list.size());
        }
        return RestResponse.error("更新失败");
    }

    @Override
    public RestResponse deleteDictDataByIds(Long[] dictCodes) {

        Boolean flag = removeByIds(Arrays.asList(dictCodes));

        return flag ? RestResponse.success("删除成功") : RestResponse.error("删除失败");
    }


}




