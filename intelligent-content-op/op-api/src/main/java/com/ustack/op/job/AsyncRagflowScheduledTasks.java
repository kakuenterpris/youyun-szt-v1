package com.ustack.op.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.emdedding.dto.ResourceDTO;
import com.ustack.feign.client.KbaseApi;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.op.mapper.RelUserResourceMapper;
import com.ustack.op.runnable.EmbeddingAndSaveRunnable;
import com.ustack.op.service.EmbeddingService;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.RelUserResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncRagflowScheduledTasks {


    @Autowired
    private RelUserResourceMapper relUserResourceMapper;

    @Autowired
    private RagFlowProcessService ragFlowProcessService;

    private final KbaseApi kbaseApi;

    private final EmbeddingService embeddingService;

    private final RelUserResourceService relUserResourceService;

    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.DiscardPolicy());

    private final RedisUtil redisUtil;

    /**
     * 同步文件知识化状态（向量化状态）
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    // @Scheduled(fixedRate = 500000)
    public void syncIndexingStatus() {
        log.info("开始处理未向量化数据，线程: {}", Thread.currentThread().getName());

        List<ResourceDTO> resourceList = relUserResourceMapper.selectUnCompleteIndexing();
        log.info("获取未向量化数据{}条", resourceList.size());
        if (CollUtil.isEmpty(resourceList)) {
            return;
        }
        for (ResourceDTO resource : resourceList) {
            if (StrUtil.isEmpty(resource.getDocumentId())) {
                continue;
            }
            String redisKey = CommonConstants.REDIS_CHUNKS_KEY + resource.getRagDatasetId() + "-" + resource.getDocumentId();
            boolean bool = redisUtil.hasKey(redisKey);
            if (bool) {
                log.info("文档 {} 正在被其他线程处理，跳过", resource.getDocumentId());
                continue;
            }
            // 设置30分钟key失效
            redisUtil.set(redisKey, true, 1800);
            try {
                // 将切片内容向量化存储到kbase中
                EmbeddingAndSaveRunnable embeddingAndSaveRunnable = new EmbeddingAndSaveRunnable(ragFlowProcessService, kbaseApi, embeddingService, relUserResourceService, resource, redisUtil);
                executor.submit(embeddingAndSaveRunnable);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("同步文件知识化状态异常，线程: ", Thread.currentThread().getName());
            }
        }
        log.info("未向量化数据处理完成");

    }
}
