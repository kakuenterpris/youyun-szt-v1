package com.ustack.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.annotation.Log;
import com.ustack.chat.entity.SysDictDataEntity;
import com.ustack.chat.entity.SysDictTypeEntity;
import com.ustack.chat.repo.SysDictTypeRepo;
import com.ustack.enums.BusinessType;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/dict/type")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "数据字典信息", description = "数据字典信息")
public class SysDictTypeController {


    @Autowired
    private SysDictTypeRepo dictTypeRepo;

    @Log(title = "字典类型", businessType = BusinessType.QUERY)
    @GetMapping("/list")
    public RestResponse list(Page<SysDictTypeEntity> page,SysDictTypeEntity dictType)
    {
        Page<SysDictTypeEntity> list = dictTypeRepo.pageList(page,dictType);
        return RestResponse.success(list);
    }


    /**
     * 查询字典类型详细
     */
    @Log(title = "字典类型", businessType = BusinessType.QUERY)
    @GetMapping(value = "/{dictId}")
    public RestResponse getInfo(@PathVariable Long dictId)
    {
        return dictTypeRepo.selectDictTypeById(dictId);
    }

    /**
     * 新增字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public RestResponse add(@RequestBody SysDictTypeEntity dict)
    {
        if (!dictTypeRepo.checkDictTypeUnique(dict))
        {
           String error =  "新增字典'" + dict.getDictName() + "'失败，字典类型已存在";
            return RestResponse.error(error);
        }
        return dictTypeRepo.insertDictType(dict);
    }

    /**
     * 修改字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public RestResponse edit(@RequestBody SysDictTypeEntity dict)
    {
        if (!dictTypeRepo.checkDictTypeUnique(dict))
        {
            String error = "修改字典'" + dict.getDictName() + "'失败，字典类型已存在";
            return RestResponse.error(error);
        }

        return dictTypeRepo.updateDictType(dict);
    }

    /**
     * 删除字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public RestResponse remove(@PathVariable Long[] dictIds)
    {
        return dictTypeRepo.deleteDictTypeByIds(dictIds);
    }


}
