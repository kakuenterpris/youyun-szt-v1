package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.SysDictTypeEntity;
import com.ustack.global.common.rest.RestResponse;

/**
* @author Lenovo
* @description 针对表【sys_dict_type】的数据库操作Service
* @createDate 2025-04-17 14:43:43
*/
public interface SysDictTypeRepo extends IService<SysDictTypeEntity> {

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */

    Page<SysDictTypeEntity> pageList(Page<SysDictTypeEntity> page, SysDictTypeEntity dictType);

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    RestResponse selectDictTypeById(Long dictId);


    /**
     * 批量删除字典信息
     *
     * @param dictIds 需要删除的字典ID
     */
    RestResponse deleteDictTypeByIds(Long[] dictIds);

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    RestResponse insertDictType(SysDictTypeEntity dictType);

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    RestResponse updateDictType(SysDictTypeEntity dictType);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    Boolean checkDictTypeUnique(SysDictTypeEntity dictType);


}
