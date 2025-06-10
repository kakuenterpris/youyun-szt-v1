package com.ustack.chat.job;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.ustack.chat.enums.ChatApiKeyEnum;
import com.ustack.chat.properties.AiConfigProperties;
import com.ustack.chat.properties.ApikeyConfigProperties;
import com.ustack.chat.repo.RelUserResourceRepo;
import com.ustack.chat.util.HttpUtils;
import com.ustack.resource.dto.IndexingStatusDTO;
import com.ustack.resource.dto.RelUserResourceDTO;
import com.ustack.resource.enums.IndexingStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncScheduledTasks {
    private final AiConfigProperties aiConfigProperties;
    private final RelUserResourceRepo relUserResourceRepo;
    private final ApikeyConfigProperties apikeyConfigProperties;

    /**
     * 同步文件知识化状态（向量化状态）
     */
    @Async("asyncTaskExecutor") // 指定自定义线程池
    @Scheduled(cron = "0 0/1 * * * ?") // 每1分钟执行一次
    public void syncIndexingStatus() {
        log.info("开始处理数据，线程: {}" , Thread.currentThread().getName());
        try {
            List<RelUserResourceDTO> resourceList = relUserResourceRepo.getIndexingList();
            if (CollUtil.isNotEmpty(resourceList)){
                for (RelUserResourceDTO resource: resourceList){
                    List<IndexingStatusDTO> indexingStatusList = this.getDocumentIndexingStatus(resource.getDatasetsId(), resource.getBatch());
                    for (IndexingStatusDTO indexingStatus: indexingStatusList){
                        IndexingStatusEnum indexingStatusEnum = IndexingStatusEnum.fromIndexingStatus(indexingStatus.getIndexing_status());
                        String indexingStatusName = null == indexingStatusEnum ? "未完成" : indexingStatusEnum.getIndexingStatusName();
                        relUserResourceRepo.updateIndexStatus(indexingStatus.getId(), indexingStatus.getIndexing_status(), indexingStatusName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("同步文件知识化状态异常，线程: " , Thread.currentThread().getName());
        }
    }

    /**
     * 获取文档嵌入状态（进度）
     *
     * @param datasetsId 知识库ID
     * @param batch 上传文档的批次号
     * @return 文档嵌入状态
     */
    private List<IndexingStatusDTO> getDocumentIndexingStatus(String datasetsId, String batch) {
        String url = aiConfigProperties.getDatasetsDocumentIndexingStatusApi();
        url = String.format(url, datasetsId, batch);
        Map map = new HashMap<>();
        List<IndexingStatusDTO> result = new ArrayList<>();
        String apikey = apikeyConfigProperties.getCustomvector();
        String response = HttpUtils.doGet(url, apikey);
//        String response = HttpUtils.doGet(url, ChatApiKeyEnum.customvector.getKey());
        //log.info("同步知识化状态响应：{}, 知识库id：{}，批次号：{}", response, datasetsId, batch);
        if (StringUtils.isNotBlank(response)) {
            Gson gson = new Gson();
            map = gson.fromJson(response, Map.class);
            Object data = map.get("data");
            if (null != data){
                try{
                    Type type = new TypeToken<List<IndexingStatusDTO>>() {}.getType();
                    result = gson.fromJson(gson.toJson(data), type);
                    //log.info("同步知识化状态响应的查询结果：{}, 知识库id：{}，批次号：{}", result, datasetsId, batch);
                } catch (Exception e) {
                    log.info("同步知识化状态响应的data：{}, 知识库id：{}，批次号：{}", data, datasetsId, batch);
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }
}
