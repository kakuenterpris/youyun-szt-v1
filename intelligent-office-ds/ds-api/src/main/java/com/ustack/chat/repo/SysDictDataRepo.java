package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.SysDictDataEntity;
import com.ustack.global.common.rest.RestResponse;

/**
* @author Lenovo
* @description 针对表【sys_dict_data】的数据库操作Service
* @createDate 2025-04-17 15:56:51
*/
public interface SysDictDataRepo extends IService<SysDictDataEntity> {

    RestResponse selectDictDataByType(String dictType);

    RestResponse selectDictDataById(Long dictCode);

    RestResponse insertDictData(SysDictDataEntity dictDataEntity);

    RestResponse updateDictData(SysDictDataEntity dictDataEntity);

    RestResponse deleteDictDataByIds(Long[] dictCodes);

    Page<SysDictDataEntity> pageList(Page<SysDictDataEntity> page,SysDictDataEntity dictDataEntity);
}
