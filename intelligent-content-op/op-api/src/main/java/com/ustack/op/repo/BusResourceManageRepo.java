package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.op.entity.BusResourceManageEntity;
import com.ustack.resource.dto.BusResourceManageDTO;
import com.ustack.resource.dto.QueryDTO;

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

    /**
     * 更新
     */
    boolean updatePreviewFileId(Long id, String previewFileId);

    /**
     * 更新
     */
    boolean updateJoinQuery(List<Integer> idList, Boolean joinQuery);

    List<BusResourceManageDTO> listByIdList(List<Integer> idList, Boolean joinQuery);

    List<BusResourceManageDTO> listFixed();

    List<BusResourceManageDTO> listResourceFloder();

    List<BusResourceManageDTO> listUnit(Integer resourceType);

    List<BusResourceManageDTO> listDep(Integer resourceType, List<String> depNumList);

    List<BusResourceManageDTO> list(String userId);

    int maxSort();

    List<BusResourceManageDTO> resourceListRight(String userId, String category, QueryDTO query);

    Integer resourceListRightCount(String userId, String category, QueryDTO query);

    List<BusResourceManageDTO> listByParentId(Integer parentId);

    List<BusResourceManageDTO> listByParentIdAndName(Integer parentId, List<String> nameList);

    List<BusResourceManageDTO> listByParentIdAndResourceType(Long parentId, Integer resourceType);

    BusResourceManageEntity resourceTypeById(Integer id);

    BusResourceManageEntity getById(Long id);
}
