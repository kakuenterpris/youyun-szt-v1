package com.ustack.op.repo;

import com.ustack.op.entity.BusResourceFolderEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.resource.dto.BusResourceFolderDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_folder(文件夹表)】的数据库操作Service
* @createDate 2025-04-23 11:32:26
*/
public interface BusResourceFolderRepo extends IService<BusResourceFolderEntity> {
    /**
     * 添加
     */
    Long add(BusResourceFolderDTO dto);

    /**
     * 逻辑删除
     */
    boolean delete(Integer id);

    /**
     * 逻辑删除
     */
    boolean deleteList(List<Integer> idList);

    /**
     * 更新
     */
    boolean update(BusResourceFolderDTO dto);

    /**
     * 列表（notDelete为true时，查询未删除的文件夹，为false时，查询所有文件夹）
     */
    List<BusResourceFolderDTO> listAll(boolean notDelete);

    /**
     * 列表（notDelete为true时，查询未删除的文件夹，为false时，查询所有文件夹）
     */
    List<BusResourceFolderDTO> listAllByType(boolean notDelete, Integer type);

    /**
     * 列表
     */
    List<BusResourceFolderDTO> listOpenView();

    /**
     * 列表（notDelete为true时，查询未删除的文件夹，为false时，查询所有文件夹）
     */
    List<BusResourceFolderDTO> listByParentId(Integer parentId, boolean notDelete);

    /**
     * 根据 ID 查询
     */
    BusResourceFolderDTO getOneById(Long id);

    int maxSort();

    List<BusResourceFolderDTO> listByParentId(Long parentId);

    boolean updateParent(BusResourceFolderDTO dto);
}
