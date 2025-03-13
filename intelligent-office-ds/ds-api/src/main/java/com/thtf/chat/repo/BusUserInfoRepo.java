package com.thtf.chat.repo;

import com.thtf.chat.entity.BusUserInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.global.common.dto.BusUserInfoDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_user_info(人员信息表)】的数据库操作Service
* @createDate 2025-02-19 17:45:24
*/
public interface BusUserInfoRepo extends IService<BusUserInfoEntity> {

    /**
     * 根据同方OA登录名查询用户信息
     */
    BusUserInfoDTO getByLoginId(String loginId);
    /**
     * 根据同方OA登录名查询用户信息
     */
    BusUserInfoDTO getByEncryptLoginId(String encryptLoginId);

    boolean deleteAll();

    List<BusUserInfoDTO> listAll();

    boolean updateEncryptLoginId(String loginId, String encryptLoginId);
}
