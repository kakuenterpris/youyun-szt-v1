package com.thtf.chat.enums;

import java.util.Objects;

/**
 * 对话api-key
 */
public enum ChatApiKeyEnum {
    // 智能助手
    common("common", "app-mBU2dqQpwlwMgylRty7SNU0M"),
    // 智能代码
    intellcode("intellcode", "app-Kw0lumNGR0yBkaJ0Kgj7FBn9"),
    // ppt大纲
    pptoutline("pptoutline", "app-dhDXmBniGUWOkn6P0QFFgCJq"),
    // 新闻稿
    newsmanuscript("newsmanuscript", "app-X9RZ4ABstSsuOqOhFm5FIsqD"),
    // 智能公文
    intelldoc("intelldoc", "app-av7ppMFMyOxLJPL1eUZxoQAo"),
    // 智能审校
    intellproofread("intellproofread", "app-seq0v7KskSKWYdrjnqXQEN4y"),
    // 发言稿
    speechscript("speechscript", "app-5hjYrcTXn6Fqe8Ds4w42G8ST"),
    // 会议纪要
    meetingsammary("meetingsammary", "app-Ek6PsrqarcupEesPLIqeypdj"),
    //数据挖掘处理
    dm("dm", "app-vdUnKScMsEKymWUVPSn8ICbJ"),
    // 智能研报
    intellreport("intellreport", "app-CF27WxWJ7xcTGE3ECCYJfUvo"),
    // 个人知识库
    customvector("customvector", "dataset-dezUJfSWgpETM8niLddTZboD"),
    //联网搜索
    netsearch("netsearch", "app-q6PgQfNd13N26n5d2Rckjt4k"),
    //数据中台
    dataCenter("dataCenter", "app-TQ4J8MMGB2iQmF3infBr14cp"),
    //推荐列表key
    recommendList("recommendList", "app-wloDympdu1wXygqiG83V0V8q"),
    ;

    private String type;
    private String key;

    ChatApiKeyEnum(String type, String key) {
        this.type = type;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public static String getKey(String type) {
        for (ChatApiKeyEnum chatApiKeyEnum : values()) {
            if (Objects.equals(chatApiKeyEnum.getType(), type)) {
                return chatApiKeyEnum.getKey();
            }
        }
        return null;
    }
}
