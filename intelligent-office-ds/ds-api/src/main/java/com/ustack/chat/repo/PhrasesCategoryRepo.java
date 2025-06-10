package com.ustack.chat.repo;

import com.ustack.chat.entity.PhrasesCategoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.phrases.dto.PhrasesCategoryDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_common_phrases_category(常用语分类表)】的数据库操作Service
* @createDate 2025-02-18 17:56:12
*/
public interface PhrasesCategoryRepo extends IService<PhrasesCategoryEntity> {
    /**
     * 添加
     */
    boolean add(PhrasesCategoryDTO dto);

    /**
     * 逻辑删除
     */
    boolean delete(Integer id);

    /**
     * 更新
     */
    boolean update(PhrasesCategoryDTO dto);

    /**
     * 列表
     */
    List<PhrasesCategoryDTO> list(Integer parentId);

    /**
     * 根据 ID 查询
     */
    PhrasesCategoryDTO getById(Integer id);
}
