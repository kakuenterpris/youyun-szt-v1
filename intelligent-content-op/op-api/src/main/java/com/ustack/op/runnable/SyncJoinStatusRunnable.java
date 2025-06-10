package com.ustack.op.runnable;

import com.ustack.feign.client.KbaseApi;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.RelUserResourceService;
import com.ustack.resource.enums.IndexingStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年05月08日
 */
@Slf4j
public class SyncJoinStatusRunnable implements Runnable {

    private final List<String> fileIdList;

    private final Boolean joinQuery;
    private final KbaseApi kbaseApi;

    private final RelUserResourceService relUserResourceService;

    public SyncJoinStatusRunnable(List<String> fileIdList, Boolean joinQuery, KbaseApi kbaseApi, RelUserResourceService relUserResourceService) {
        this.fileIdList = fileIdList;
        this.joinQuery = joinQuery;
        this.kbaseApi = kbaseApi;
        this.relUserResourceService = relUserResourceService;
    }

    @Override
    public void run() {
        this.handler();
    }

    private void handler() {
        Integer joinQueryValid = null;
        if (joinQuery) {
            joinQueryValid = 1;
        } else {
            joinQueryValid = 0;
        }
        RestResponse restResponse = kbaseApi.updateJoinValid(fileIdList, joinQueryValid);
        if (null == restResponse || restResponse.getCode() != 200) {
            log.error("更新知识库现行有效状态失败，文件id为{}", fileIdList);
            relUserResourceService.updateIndexStatus(fileIdList, IndexingStatusEnum.SYNC_JOIN_QUERY_ERROR.getIndexingStatus(), IndexingStatusEnum.SYNC_JOIN_QUERY_ERROR.getIndexingStatusName());

        } else {
            boolean result = (boolean) restResponse.getData();
            if (!result) {
                relUserResourceService.updateIndexStatus(fileIdList, IndexingStatusEnum.SYNC_JOIN_QUERY_ERROR.getIndexingStatus(), IndexingStatusEnum.SYNC_JOIN_QUERY_ERROR.getIndexingStatusName());
            }
        }
    }
}
