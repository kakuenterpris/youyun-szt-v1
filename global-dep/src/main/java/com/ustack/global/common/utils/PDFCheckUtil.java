package com.ustack.global.common.utils;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PDFCheckUtil {


    /**
     * 判断是否为双层PDF（文本层 + 图像层）
     */
    public static boolean isDoubleLayerPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            return hasTextLayer(document) && hasImageLayer(document);
        }
    }

    // ------------------- 核心检测方法（修复Iterable.isEmpty问题） -------------------

    /**
     * 检查文本层
     */
    private static boolean hasTextLayer(PDDocument document) throws IOException {
        String text = new PDFTextStripper().getText(document);
        return text != null && !text.trim().isEmpty();
    }

    /**
     * 检查图像层
     */
    private static boolean hasImageLayer(PDDocument document) {
        for (PDPage page : document.getPages()) {
            PDResources resources = page.getResources();
            if (isBlankPage(resources, page)) {
                continue;
            }

            // Iterable.isEmpty -> 检查迭代器存在性
            Iterable<COSName> xObjectNames = resources.getXObjectNames();
            if (!xObjectNames.iterator().hasNext()) {
                continue;
            }

            for (COSName name : xObjectNames) {
                try {
                    if (resources.isImageXObject(name)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(name);
                        if (isValidImage(image)) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    // 忽略无法解析的图像
                }
            }
        }
        return false;
    }

    /**
     * 优化：判断空白页
     */
    private static boolean isBlankPage(PDResources resources, PDPage page) {
        // 使用迭代器检查XObject存在性
        boolean hasXObjects = resources.getXObjectNames().iterator().hasNext();
        PDRectangle cropBox = page.getCropBox();
        return !hasXObjects && Objects.equals(cropBox, new PDRectangle(0, 0));
    }

    /**
     * 过滤无效图像
     */
    private static boolean isValidImage(PDImageXObject image) {
        return image.getWidth() > 10 && image.getHeight() > 10;
    }

    /**
     * 方法重载：支持路径参数
     */
    public static boolean isDoubleLayerPdf(String filePath) throws IOException {
        return isDoubleLayerPdf(new File(filePath));
    }
}
