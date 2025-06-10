package com.ustack.chat.mapper;

import com.ustack.chat.entity.PhrasesEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author allm
* @description 针对表【bus_common_phrases(常用语表)】的数据库操作Mapper
* @createDate 2025-02-18 17:52:56
* @Entity com.ustack.chat.entity.BusCommonPhrasesEntity
*/
@Mapper
public interface PhrasesMapper extends BaseMapper<PhrasesEntity> {

}




