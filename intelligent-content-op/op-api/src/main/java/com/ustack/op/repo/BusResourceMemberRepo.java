package com.ustack.op.repo;

import com.ustack.op.entity.BusResourceMemberEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.resource.dto.BusResourceMemberDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_member(文件夹成员权限表)】的数据库操作Service
* @createDate 2025-04-23 11:43:16
*/
public interface BusResourceMemberRepo extends IService<BusResourceMemberEntity> {
    /**
     * 添加
     */
    boolean add(BusResourceMemberDTO dto);
    /**
     * 添加
     */
    void add(List<BusResourceMemberDTO> list, Long folderId);

    /**
     * 逻辑删除
     */
    boolean delete(Long folderId);

    /**
     * 更新
     */
    boolean update(BusResourceMemberDTO dto);

    /**
     * 列表
     */
    List<BusResourceMemberDTO> list(Integer folderId);

    /**
     * 列表
     */
    List<BusResourceMemberDTO> listByUser(String userId);

    /**
     * 列表
     */
    List<BusResourceMemberDTO> listAdminByUser(String userId);

    /**
     * 列表
     */
    List<BusResourceMemberDTO> listMemberByUser(String userId);

    /**
     * 列表
     */
    List<BusResourceMemberDTO> listMemberAndViewAuthByUser(String userId);

    /**
     * 根据 ID 查询
     */
    BusResourceMemberDTO getByUser(Integer folderId, String userId);

    /**
     * 根据 ID 查询
     */
    BusResourceMemberDTO getById(Integer id);

}
