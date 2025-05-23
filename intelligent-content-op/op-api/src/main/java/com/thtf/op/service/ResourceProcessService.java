package com.thtf.op.service;

import com.thtf.emdedding.dto.QueryKmDTO;
import com.thtf.emdedding.dto.RagProcessDTO;
import com.thtf.global.common.rest.RestResponse;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
public interface ResourceProcessService {

    RestResponse execute(List<RagProcessDTO> fileIdList);

    // RestResponse query(QueryKmDTO queryKmDTO);

    RestResponse query(QueryKmDTO queryKmDTO);

    boolean delete(Long resourceId, String documentId, String fileId, String embeddingConfigCode);

    void updateJoinQuery(Boolean joinQuery, List<String> idList);

}
