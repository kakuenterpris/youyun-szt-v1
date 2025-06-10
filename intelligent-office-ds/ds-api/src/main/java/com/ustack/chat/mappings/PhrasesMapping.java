package com.ustack.chat.mappings;

import com.ustack.chat.entity.PhrasesEntity;
import com.ustack.phrases.dto.PhrasesDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface PhrasesMapping {

    PhrasesEntity dto2Entity(PhrasesDTO param);

    PhrasesDTO entity2Dto(PhrasesEntity param);


}
