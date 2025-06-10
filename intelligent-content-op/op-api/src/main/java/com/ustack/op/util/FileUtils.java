package com.ustack.op.util;

import com.github.pagehelper.util.StringUtil;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangwei
 * @date 2025年03月28日
 */
public class FileUtils {

    /**
     * file 转 MultipartFile
     * @param file
     * @return
     * @throws IOException
     */

    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return new MockMultipartFile(
                file.getName(),              // 文件名
                file.getName(),              // 原始文件名
                "application/octet-stream",  // 文件类型
                inputStream                  // 文件流
        );
    }

    /**
     * BigDecimal 转 String
     * @param list
     * @return
     */
    public static String listBigdecimalToString(List<BigDecimal> list) {
        StringBuffer embeddingBuffer = new StringBuffer();
        // 遍历集合去掉每个字符串得换行
        // embeddings.forEach(s -> s.replaceAll("\n", ""));
        for (BigDecimal bigDecimal : list) {
            String str = bigDecimal.toString();
            if (embeddingBuffer.length() > 0) {
                embeddingBuffer.append(",");
            }
            if (str.contains("\n")) {
                embeddingBuffer.append(str.replaceAll("\n", ""));
            } else {
                embeddingBuffer.append(str.trim());
            }
        }
        String str = "[" + embeddingBuffer.toString() + "]";
        return str;
    }

    /**
     * List<String> 转 String
     * @param list
     * @return
     */
    public static String listStringToString(List<String> list) {
        StringBuffer embeddingBuffer = new StringBuffer();
        // 遍历集合去掉每个字符串得换行
        for (String str : list) {
            if (embeddingBuffer.length() > 0) {
                embeddingBuffer.append(",");
            }
            if (str.contains("\n")) {
                embeddingBuffer.append(str.replaceAll("\n", ""));
            } else {
                embeddingBuffer.append(str.trim());
            }

        }
        return embeddingBuffer.toString();
    }

    /**
     * 取前缀前面得名字
     * @param name
     * @return
     */
    public static String getFileNameNoSuffix(String name) {
        if (StringUtil.isEmpty(name)) {
            return null;
        }
        return name.substring(0, name.lastIndexOf("."));
    }
}
