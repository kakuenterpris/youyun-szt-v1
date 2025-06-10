package com.ustack.chat.enums;

import java.util.Objects;

/**
 * 对话api-key
 */
public enum ChatApiKeyEnum {
    // 智能助手
    common("common", "app-3P4HVPVeoNl6L1ZhUZl2evQl"),
    // 智能代码
    intellcode("intellcode", "app-MF0v0yXl3s33wPVaepsvBVMc"),
    // ppt大纲
    pptoutline("pptoutline", "app-KWgDvVooc0zskI0eY7DyrbIU"),
    // 新闻稿
    newsmanuscript("newsmanuscript", "app-oSzsdmDdC3O9g6zBR6yhuORK"),
    // 智能公文
    intelldoc("intelldoc", "app-z1ev8jWWWq0VOpCIFkHCsSEc"),
    // 智能审校
    intellproofread("intellproofread", "app-z8Hrj4mj3X3uGVnGIjcrU1pb"),
    // 发言稿
    speechscript("speechscript", "app-uZ0x3XaSyB0D4yaxS4iraOhS"),
    // 会议纪要
    meetingsammary("meetingsammary", "app-0rboba0jQweVfdES4dmt3JJw"),
    //数据挖掘处理
    dm("dm", "app-7MlA0qIGe9CZUIXKP8JHyiBH"),
    // 智能研报
    intellreport("intellreport", "app-yWYyVMfDmq0us7al5bg7JVYG"),
    // 个人知识库
    customvector("customvector", "dataset-kNfh2qv11cCdKv1yg9JjEctT"),
    //联网搜索
    netsearch("netsearch", "app-fosI5MnjkkHQJzwRJDveAa3O"),
    //数据中台
    dataCenter("dataCenter", "app-TQ4J8MMGB2iQmF3infBr14cp"),
    //推荐列表key
    recommendList("recommendList", "app-ONOtPOsaMB2Gzdmj8eBVhEKH"),
    // 意图识别
    intent("intent", "app-ZloxQh9n6H6ztIa4n96qKKIj"),
    // 新联网搜索
    newNetSearch("newNetSearch", "app-fosI5MnjkkHQJzwRJDveAa3O"),
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
