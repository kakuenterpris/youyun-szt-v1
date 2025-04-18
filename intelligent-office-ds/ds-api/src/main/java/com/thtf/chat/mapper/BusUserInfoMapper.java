package com.thtf.chat.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.access.vo.UserInfoVO;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author allm
* @description 针对表【bus_user_info(人员信息表)】的数据库操作Mapper
* @createDate 2025-02-19 17:45:24
* @Entity com.thtf.chat.entity.BusUserInfoEntity
*/
public interface BusUserInfoMapper extends BaseMapper<BusUserInfoEntity> {

    Page<UserInfoVO> selectPageByVO(Page<UserInfoDto> pages, UserInfoVO vo);
}




