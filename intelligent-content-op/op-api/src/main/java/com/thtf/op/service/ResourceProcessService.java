package com.thtf.op.service;

import com.thtf.emdedding.dto.QueryKmDTO;
import com.thtf.emdedding.dto.RagProcessDTO;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.resource.dto.BusResourceManageDTO;

import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
public interface ResourceProcessService {

    void execute(List<RagProcessDTO> fileIdList);

    // RestResponse query(QueryKmDTO queryKmDTO);

    RestResponse query(QueryKmDTO queryKmDTO);

    boolean delete(Long resourceId, String documentId, String fileId, String embeddingConfigCode);

    void updateJoinQuery(Boolean joinQuery, List<String> idList);

    // boolean deleteByFolderId(String folderId, String embeddingConfigCode);

}
