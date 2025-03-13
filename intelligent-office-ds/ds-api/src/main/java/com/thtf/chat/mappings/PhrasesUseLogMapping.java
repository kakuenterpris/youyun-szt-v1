package com.thtf.chat.mappings;

import com.thtf.chat.entity.PhrasesUseLogEntity;
import com.thtf.phrases.dto.PhrasesUseLogDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface PhrasesUseLogMapping {

    PhrasesUseLogEntity dto2Entity(PhrasesUseLogDTO param);

    PhrasesUseLogDTO entity2Dto(PhrasesUseLogEntity param);

}
