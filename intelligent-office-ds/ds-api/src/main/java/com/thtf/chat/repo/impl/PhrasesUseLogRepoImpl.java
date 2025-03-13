package com.thtf.chat.repo.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.PhrasesUseLogEntity;
import com.thtf.chat.mappings.PhrasesUseLogMapping;
import com.thtf.chat.repo.PhrasesUseLogRepo;
import com.thtf.chat.mapper.PhrasesUseLogMapper;
import com.thtf.global.common.utils.Linq;
import com.thtf.phrases.dto.PhrasesUseLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author allm
* @description 针对表【bus_common_phrases_use_log(常用语使用记录表)】的数据库操作Service实现
* @createDate 2025-02-18 17:56:19
*/
@Service
@RequiredArgsConstructor
public class PhrasesUseLogRepoImpl extends ServiceImpl<PhrasesUseLogMapper, PhrasesUseLogEntity>
    implements PhrasesUseLogRepo {
    private final PhrasesUseLogMapping mapping;

    @Override
    public boolean add(PhrasesUseLogDTO dto) {
        PhrasesUseLogEntity entity = mapping.dto2Entity(dto);
        entity.setId(null);
        entity.setUseTime(new Date());
        return save(entity);
    }

    @Override
    public List<PhrasesUseLogDTO> list(Integer phraseId) {
        List<PhrasesUseLogEntity> list = lambdaQuery()
                .eq(PhrasesUseLogEntity::getPhraseId, phraseId)
                .eq(PhrasesUseLogEntity::getDeleted, false)
                .list();
        return Linq.select(list, mapping::entity2Dto);
    }

    @Override
    public Integer count(Integer phraseId) {
        List<PhrasesUseLogEntity> list = lambdaQuery()
                .eq(PhrasesUseLogEntity::getPhraseId, phraseId)
                .eq(PhrasesUseLogEntity::getDeleted, false)
                .list();
        return CollUtil.isEmpty(list) ? 0 : list.size();
    }

}




