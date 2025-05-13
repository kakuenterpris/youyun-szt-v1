package com.thtf.op.mappings;

import com.thtf.op.entity.BusResourceFavoriteEntity;
import com.thtf.resource.dto.BusResourceFavoriteDTO;
import com.thtf.resource.vo.BusResourceFavoriteVO;
import org.mapstruct.Mapper;

/**
 * @author Liyingzheng
 * @data 2025/5/8 16:56
 * @describe 对象转换
 */
@Mapper(componentModel = "spring")
public interface BusResourceFavoriteMapping {

    BusResourceFavoriteEntity dto2Entity(BusResourceFavoriteDTO param);

    BusResourceFavoriteDTO entity2Dto(BusResourceFavoriteEntity param);

    BusResourceFavoriteVO entity2Vo(BusResourceFavoriteEntity param);
}
