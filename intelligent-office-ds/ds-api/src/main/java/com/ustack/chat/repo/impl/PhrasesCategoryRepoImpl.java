package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.PhrasesCategoryEntity;
import com.ustack.chat.mappings.PhrasesCategoryMapping;
import com.ustack.chat.repo.PhrasesCategoryRepo;
import com.ustack.chat.mapper.PhrasesCategoryMapper;
import com.ustack.global.common.utils.Linq;
import com.ustack.phrases.dto.PhrasesCategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_common_phrases_category(常用语分类表)】的数据库操作Service实现
* @createDate 2025-02-18 17:56:12
*/
@Service
@RequiredArgsConstructor
public class PhrasesCategoryRepoImpl extends ServiceImpl<PhrasesCategoryMapper, PhrasesCategoryEntity>
    implements PhrasesCategoryRepo {
    private final PhrasesCategoryMapping mapping;

    @Override
    public boolean add(PhrasesCategoryDTO dto) {
        PhrasesCategoryEntity entity = mapping.dto2Entity(dto);
        entity.setId(null);
        return save(entity);
    }

    @Override
    public boolean delete(Integer id) {
        return lambdaUpdate()
                .set(PhrasesCategoryEntity::getDeleted, true)
                .eq(PhrasesCategoryEntity::getId, id)
                .eq(PhrasesCategoryEntity::getDeleted, false)
                .update(new PhrasesCategoryEntity());
    }

    @Override
    public boolean update(PhrasesCategoryDTO dto) {
        PhrasesCategoryEntity entity = lambdaQuery().eq(PhrasesCategoryEntity::getId, dto.getId()).one();
        entity.setName(dto.getName());
        entity.setComment(dto.getComment());
        entity.setCommentRich(dto.getCommentRich());
        entity.setOrderBy(dto.getOrderBy());
        entity.setParentId(dto.getParentId());

        return lambdaUpdate()
                .eq(PhrasesCategoryEntity::getId, dto.getId())
                .eq(PhrasesCategoryEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public List<PhrasesCategoryDTO> list(Integer parentId) {
        List<PhrasesCategoryEntity> list = lambdaQuery()
                .eq(PhrasesCategoryEntity::getParentId, parentId)
                .eq(PhrasesCategoryEntity::getDeleted, false)
                .list();
        return Linq.select(list, mapping::entity2Dto);
    }

    @Override
    public PhrasesCategoryDTO getById(Integer id) {
        List<PhrasesCategoryEntity> list = lambdaQuery()
                .eq(PhrasesCategoryEntity::getId, id)
                .eq(PhrasesCategoryEntity::getDeleted, false)
                .list();
        return mapping.entity2Dto(Linq.first(list));
    }

}




