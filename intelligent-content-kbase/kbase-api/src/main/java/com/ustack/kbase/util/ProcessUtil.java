package com.ustack.kbase.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangwei
 * @date 2025年05月09日
 */
@Slf4j
public class ProcessUtil {

    /**
     * 处理字符串中的双引号，进行转义
     */
    public static String escapeHtml(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        return str.replace("\"", "\\\"");
    }
    /**
     * 处理字符串中的特殊字符，进行转义
     */
    public static String escapeHtml2(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        // return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'").replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'");
    }
}
