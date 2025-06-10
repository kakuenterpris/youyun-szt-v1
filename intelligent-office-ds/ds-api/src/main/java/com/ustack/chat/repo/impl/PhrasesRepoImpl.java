package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.PhrasesEntity;
import com.ustack.chat.mappings.PhrasesMapping;
import com.ustack.chat.repo.PhrasesRepo;
import com.ustack.chat.mapper.PhrasesMapper;
import com.ustack.global.common.utils.Linq;
import com.ustack.phrases.dto.PhrasesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_common_phrases(常用语表)】的数据库操作Service实现
* @createDate 2025-02-18 17:52:56
*/
@Service
@RequiredArgsConstructor
public class PhrasesRepoImpl extends ServiceImpl<PhrasesMapper, PhrasesEntity>
    implements PhrasesRepo {
    private final PhrasesMapping mapping;

    @Override
    public boolean add(PhrasesDTO dto) {
        PhrasesEntity entity = mapping.dto2Entity(dto);
        entity.setId(null);
        return save(entity);
    }

    @Override
    public boolean delete(Integer id) {
        return lambdaUpdate()
                .set(PhrasesEntity::getDeleted, true)
                .eq(PhrasesEntity::getId, id)
                .eq(PhrasesEntity::getDeleted, false)
                .update(new PhrasesEntity());
    }

    @Override
    public boolean update(PhrasesDTO dto) {
        PhrasesEntity entity = lambdaQuery().eq(PhrasesEntity::getId, dto.getId()).one();
        entity.setContent(dto.getContent());
        entity.setOrderBy(dto.getOrderBy());

        return lambdaUpdate()
                .eq(PhrasesEntity::getId, dto.getId())
                .eq(PhrasesEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public List<PhrasesDTO> list(String userId) {
        List<PhrasesEntity> list = lambdaQuery()
                .eq(PhrasesEntity::getCreateUserId, userId)
                .eq(PhrasesEntity::getDeleted, false)
                .orderByDesc(PhrasesEntity::getOrderBy)
                .orderByDesc(PhrasesEntity::getWeight)
                .list();
        return Linq.select(list, mapping::entity2Dto);
    }

    @Override
    public PhrasesDTO getByUserId(String userId) {
        List<PhrasesEntity> list = lambdaQuery()
                .eq(PhrasesEntity::getCreateUserId, userId)
                .eq(PhrasesEntity::getDeleted, false)
                .list();
        return mapping.entity2Dto(Linq.first(list));
    }
}




