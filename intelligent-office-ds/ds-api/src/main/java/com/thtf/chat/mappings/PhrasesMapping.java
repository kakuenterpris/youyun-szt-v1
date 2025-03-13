package com.thtf.chat.mappings;

import com.thtf.chat.entity.PhrasesEntity;
import com.thtf.phrases.dto.PhrasesDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface PhrasesMapping {

    PhrasesEntity dto2Entity(PhrasesDTO param);

    PhrasesDTO entity2Dto(PhrasesEntity param);


}
