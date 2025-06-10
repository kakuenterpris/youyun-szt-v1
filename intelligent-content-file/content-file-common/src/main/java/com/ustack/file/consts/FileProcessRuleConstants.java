package com.ustack.file.consts;

/**
 * @Description: 个人知识库文件知识库处理常量
 * @author：linxin
 * @ClassName: FileProcessRuleConstants
 * @Date: 2025-02-24 13:13
 */
public interface FileProcessRuleConstants {


    interface IndexingTechnique {
        // 高质量模式
        String high = "high_quality";
        // 经济模式
        String economy = "economy";
    }

    interface ProcessMode {
        // automatic 自动
        String auto = "automatic";
        // custom 自定义
        String custom = "custom";
    }


    interface PreProcessingRule {
        // 替换连续空格、换行符、制表符
        String remove_extra_spaces = "remove_extra_spaces";

        // 是否选中该规则，不传入文档 ID 时代表默认值;
        String remove_urls_emails = "remove_urls_emails";
    }


    interface Segmentation {

        String separator = "###";

        Integer max_tokens = 1200;

        Integer chunk_overlap  = 200;

    }

}
