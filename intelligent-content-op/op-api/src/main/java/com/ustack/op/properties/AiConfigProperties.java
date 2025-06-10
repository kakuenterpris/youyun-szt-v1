package com.ustack.op.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2025年02月19日
 */
@ConfigurationProperties(prefix = "ai")
@Component
@Data
public class AiConfigProperties {

    private String answerApi;

    private String recommendListApi;

    private String answerApiKey;

    private String historyChatApi;

    private String conversationsChatApi;

    private String deleteConversationsApi;

    private String personDatasetsCreateApi;

    private String vectorQueryApi;

    private String cnkiVectorQueryApi;

    private String uploadFileApi;

    private String datasetsDocumentListApi;

    private String datasetsDocumentSegmentApi;

    private String datasetsDeleteDocumentApi;

    private String datasetsDocumentIndexingStatusApi;

    private String renameConversationApi;

    private String stopConversationApi;

    private String chatNetSearchApi;

    private String dataCenterChatApi;

    private String newNetworkSearchApi;

    private String sliceApi;

    private String sliceTempApi;

    private String embeddingApi;

    private String embeddingTempApi;
}
