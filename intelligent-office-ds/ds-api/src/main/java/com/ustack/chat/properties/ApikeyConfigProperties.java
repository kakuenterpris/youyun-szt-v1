package com.ustack.chat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @date 2025年02月19日
 */
@ConfigurationProperties(prefix = "apikey")
@Component
@Data
public class ApikeyConfigProperties {

    // 智能助手
    private String common;
    // 智能代码
    private String intellcode;
    // ppt大纲
    private String pptoutline;
    // 新闻稿
    private String newsmanuscript;
    // 智能公文
    private String intelldoc;
    // 智能审校
    private String intellproofread;
    //  发言稿
    private String speechscript;
    // 会议纪要
    private String meetingsammary;
    //  数据挖掘处理
    private String dm;
    //  智能研报
    private String intellreport;
    //  个人知识库
    private String customvector;
    //  联网搜索--暂不可用
    private String netsearch;
    //  数据中台
    private String dataCenter;
    //  推荐列表key
    private String recommendList;
    //  意图识别
    private String intent;
    //  新联网搜索  --doc没有
    private String newNetSearch;
    //  意图识别--主体
    private String intentMain;
    // 意图识别--工作流版本
    private String intentWorkflowVersion;
    // 意图识别核心
    private String intentCore;
}
