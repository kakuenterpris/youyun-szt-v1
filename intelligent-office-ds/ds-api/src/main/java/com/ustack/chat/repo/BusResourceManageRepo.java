package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.BusResourceManageEntity;
import com.ustack.resource.dto.BusResourceManageDTO;

import java.util.List;

public interface BusResourceManageRepo extends IService<BusResourceManageEntity> {
    /**
     * 添加
     */
    Long add(BusResourceManageDTO dto);

    /**
     * 删除
     */
    boolean delete(Integer id);

    /**
     * 更新
     */
    boolean update(BusResourceManageDTO dto);

    /**
     * 更新
     */
    boolean updateParentId(Integer id, Integer parentId);

    /**
     * 更新
     */
    boolean updateSort(Integer id, Integer sort);

    List<BusResourceManageDTO> listFixed();

    List<BusResourceManageDTO> listResourceFloder();

    List<BusResourceManageDTO> listUnit(Integer resourceType);

    List<BusResourceManageDTO> listDep(Integer resourceType, List<String> depNumList);

    List<BusResourceManageDTO> list(String userId);

    int maxSort();

    List<BusResourceManageDTO> resourceListRight(String userId, String name, Integer parentId, String category);

    List<BusResourceManageDTO> listByParentId(Integer parentId);

    List<BusResourceManageDTO> listByParentIdAndName(Integer parentId, List<String> nameList);

    List<BusResourceManageDTO> listByParentIdAndResourceType(Long parentId, Integer resourceType);

    BusResourceManageEntity resourceTypeById(Integer id);

    BusResourceManageEntity getById(Long id);
}
