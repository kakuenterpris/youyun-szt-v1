package com.ustack.file.dto.knowledgeLab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreProcessingRule{

        /**
         * 预处理规则的唯一标识符:
         *      remove_extra_spaces 替换连续空格、换行符、制表符
         *      remove_urls_emails 删除 URL、电子邮件地址;
         */
        private String id;

        /**
         * (bool) 是否选中该规则，不传入文档 ID 时代表默认值;
         */
        private Boolean enabled;
    }
