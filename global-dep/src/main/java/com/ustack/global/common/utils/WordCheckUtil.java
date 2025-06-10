package com.ustack.global.common.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFSettings;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WordCheckUtil {

    /**
     * 判断Word文件是否使用了模板
     *
     * @param filePath Word文件路径
     * @return 如果文件使用了模板，则返回true，否则返回false
     */
    public static boolean isTemplateUsed(String filePath) {
        File file = new File(filePath);
        try (InputStream inputStream = new FileInputStream(file)) {
            return isTemplateUsed(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断Word文件是否使用了模板
     *
     * @param inputStream Word文件输入流
     * @return 如果文件使用了模板，则返回true，否则返回false
     */
    public static boolean isTemplateUsed(InputStream inputStream) {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            // 获取文档的设置部分
            XWPFSettings settings = document.getSettings();
            if (settings == null) {
                return false;
            }

            // 获取CTSettings对象
            CTSettings ctSettings = settings.getCTSettings();

            // 检查模板相关属性
            CTRel attachedTemplate = ctSettings.getAttachedTemplate();
            if (attachedTemplate != null) {
                return true;
            }
            CTOnOff linkStyles = ctSettings.getLinkStyles();
            // 检查是否启用了模板链接
            if (linkStyles != null) {
                return true;
            }

            // 检查其他可能的模板相关属性
            // 可以根据实际需求添加更多判断逻辑

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

//    /**
//     * 检测文档属性中的模板标记
//     */
//    private static boolean hasTemplateCustomProperties(XWPFDocument document) {
//        return document.getProperties().getExtendedProperties().getUnderlyingProperties()
//                .getCustomProperties().stream()
//                .anyMatch(prop ->
//                        prop.getName().startsWith("TEMPLATE_") // 自定义属性前缀
//                );
//    }
}