package com.ustack.chat.repo;

import com.ustack.chat.entity.PhrasesUseLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.phrases.dto.PhrasesUseLogDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_common_phrases_use_log(常用语使用记录表)】的数据库操作Service
* @createDate 2025-02-18 17:56:19
*/
public interface PhrasesUseLogRepo extends IService<PhrasesUseLogEntity> {

    /**
     * 添加
     */
    boolean add(PhrasesUseLogDTO dto);

    /**
     * 列表
     */
    List<PhrasesUseLogDTO> list(Integer phraseId);

    /**
     * 计数
     */
    Integer count(Integer phraseId);
}
