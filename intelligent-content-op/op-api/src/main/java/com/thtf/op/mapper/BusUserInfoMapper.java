package com.thtf.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.global.common.dto.SystemUser;
import com.thtf.op.entity.BusUserInfoEntity;
import org.apache.ibatis.annotations.Select;

/**
* @author allm
* @description 针对表【bus_user_info(人员信息表)】的数据库操作Mapper
* @createDate 2025-02-19 17:45:24
* @Entity com.thtf.op.entity.BusUserInfoEntity
*/
public interface BusUserInfoMapper extends BaseMapper<BusUserInfoEntity> {

    //通过userId查询用户信息
    @Select("SELECT * FROM bus_user_info WHERE user_id = #{userId}")
    SystemUser getUserInfoByUserId(String userId);

}




