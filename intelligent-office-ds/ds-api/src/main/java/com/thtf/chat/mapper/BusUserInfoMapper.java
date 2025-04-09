package com.thtf.chat.mapper;

import com.thtf.chat.entity.BusUserInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author allm
* @description 针对表【bus_user_info(人员信息表)】的数据库操作Mapper
* @createDate 2025-02-19 17:45:24
* @Entity com.thtf.chat.entity.BusUserInfoEntity
*/
@Mapper
public interface BusUserInfoMapper extends BaseMapper<BusUserInfoEntity> {

    @Select("select * from bus_user_info where login_id = #{username}")
    BusUserInfoEntity selectByLogin_id(String username);
}




