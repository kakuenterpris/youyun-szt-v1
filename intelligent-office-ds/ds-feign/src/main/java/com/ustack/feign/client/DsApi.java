package com.ustack.feign.client;

import com.ustack.feign.config.DsFeignAutoconfiguration;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: 统一登录feign client
 * @author：linxin
 * @ClassName: DsApi
 * @Date: 2025-02-20 10:11
 */
@FeignClient(name = "chat-api", configuration = DsFeignAutoconfiguration.class)
//@FeignClient(name = "ds-api", url = "http://localhost:8080/", configuration = DsFeignAutoconfiguration.class)
public interface DsApi {

    /**
     * 根据fileId更新有云documentId和batch
     */
    @PostMapping("/api/v1/resource/updateInfoFromDify")
    RestResponse updateInfoFromDify(@RequestParam String fileId, @RequestParam String documentId, @RequestParam String batch);
    /**
     * 更新向量化状态
     */
    @PostMapping("/api/v1/resource/updateIndexStatus")
    RestResponse updateIndexStatus(@RequestParam String documentId, @RequestParam String indexingStatus, @RequestParam String indexingStatusName);
}
