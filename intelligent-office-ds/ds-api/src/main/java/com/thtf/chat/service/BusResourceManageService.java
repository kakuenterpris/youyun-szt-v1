package com.thtf.chat.service;

import com.thtf.global.common.rest.RestResponse;
import com.thtf.resource.dto.BusResourceManageDTO;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_resource_manage】的数据库操作Service
 * @createDate 2025-02-18 17:57:12
 */
public interface BusResourceManageService {

    RestResponse resourceTreeListLeft();

    RestResponse resourceSingleTree(String category);

    RestResponse resourceListRight(String name, Integer parentId);

    RestResponse saveResource(BusResourceManageDTO resourceManageDTO);

    RestResponse updateResource(BusResourceManageDTO resourceManageDTO);

    RestResponse deleteById(Integer id);

    RestResponse updateInfoFromDify(String fileId, String documentId, String batch);

    RestResponse updateIndexStatus(String documentId, String indexingStatus, String indexingStatusName);

    RestResponse resourceSegmentList(Integer id);

    RestResponse moveNode(Integer operateId, Integer targetId, String operateType);

    RestResponse updateParentId(Integer id, Integer originParentId, Integer newParentId);

    RestResponse updateSort(List<BusResourceManageDTO> list);

    RestResponse syncDifyDocument(String datasetId);

}
