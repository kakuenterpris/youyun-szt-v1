package com.ustack.chat.repo;

import com.ustack.chat.entity.BusDepInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.login.dto.BusDepInfoDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_dep_info(部门信息表)】的数据库操作Service
* @createDate 2025-02-28 17:04:44
*/
public interface BusDepInfoRepo extends IService<BusDepInfoEntity> {
    List<BusDepInfoDTO> listAll();
    BusDepInfoDTO getByDepNum(String depNum);
    List<BusDepInfoDTO> listAllSup(String depNum);
    List<BusDepInfoDTO> listAllChild(String depNum);
}
