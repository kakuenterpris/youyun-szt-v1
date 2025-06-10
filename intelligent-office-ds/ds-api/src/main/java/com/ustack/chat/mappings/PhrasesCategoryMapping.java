package com.ustack.chat.mappings;

import com.ustack.chat.entity.PhrasesCategoryEntity;
import com.ustack.phrases.dto.PhrasesCategoryDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface PhrasesCategoryMapping {

    PhrasesCategoryEntity dto2Entity(PhrasesCategoryDTO param);

    PhrasesCategoryDTO entity2Dto(PhrasesCategoryEntity param);

}
