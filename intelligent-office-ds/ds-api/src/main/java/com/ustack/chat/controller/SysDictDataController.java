package com.ustack.chat.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.annotation.Log;
import com.ustack.chat.entity.SysDictDataEntity;
import com.ustack.chat.repo.SysDictDataRepo;
import com.ustack.enums.BusinessType;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 数据字典信息
 *
 * @author zhoufei
 * @date 2025年04月17日
 */
@RestController
@RequestMapping("/api/v1/dict/data")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "查询数据字典接口", description = "查询数据字典接口")
public class SysDictDataController {

    private final SysDictDataRepo dictDataRepo;


    /**
     * 字典列表查询
     * @param dictDataEntity
     * @return
     */
    @Log(title = "字典类型", businessType = BusinessType.QUERY)
    @GetMapping("/list")
    @Operation(summary = "字典列表查询")
    public RestResponse list(Page<SysDictDataEntity> page,SysDictDataEntity dictDataEntity) {
        Page<SysDictDataEntity> list = dictDataRepo.pageList(page,dictDataEntity);
        return RestResponse.success(list);
    }

    /**
     * 查询字典数据详细
     */
    @Log(title = "字典类型", businessType = BusinessType.QUERY)
    @GetMapping("/{dictCode}")
    @Operation(summary = "查询字典数据详细")
    public RestResponse getInfo(@PathVariable Long dictCode){
        return dictDataRepo.selectDictDataById(dictCode);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @Log(title = "字典类型", businessType = BusinessType.QUERY)
    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型查询字典数据")
    public RestResponse getDataByType(@PathVariable String dictType){
        return dictDataRepo.selectDictDataByType(dictType);
    }

    /**
     * 新增字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Operation(summary = "新增字典类型")
    public RestResponse add(@RequestBody SysDictDataEntity dictDataEntity) {
        return dictDataRepo.insertDictData(dictDataEntity);
    }


    /**
     * 修改字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @Operation(summary = "修改字典类型")
    public RestResponse edit(@RequestBody SysDictDataEntity dictDataEntity) {
        return dictDataRepo.updateDictData(dictDataEntity);
    }


    /**
     * 删除字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{dictCodes}")
    @Operation(summary = "删除字典类型")
    public RestResponse remove(@PathVariable Long[] dictCodes){
        return dictDataRepo.deleteDictDataByIds(dictCodes);
    }

}
