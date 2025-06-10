package com.ustack.chat.mappings;

import com.ustack.chat.entity.PhrasesUseLogEntity;
import com.ustack.phrases.dto.PhrasesUseLogDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface PhrasesUseLogMapping {

    PhrasesUseLogEntity dto2Entity(PhrasesUseLogDTO param);

    PhrasesUseLogDTO entity2Dto(PhrasesUseLogEntity param);

}
