package com.thtf.chat.mappings;

import com.thtf.chat.entity.PhrasesCategoryEntity;
import com.thtf.phrases.dto.PhrasesCategoryDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface PhrasesCategoryMapping {

    PhrasesCategoryEntity dto2Entity(PhrasesCategoryDTO param);

    PhrasesCategoryDTO entity2Dto(PhrasesCategoryEntity param);

}
