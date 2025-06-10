package com.ustack.file.utils;

import cn.hutool.core.codec.Base64Encoder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description : 基于 Thumbnails 的图片处理
 * @Author : LinXin
 * @ClassName : ImageUtils
 * @Date: 2021-03-12 13:33
 */
public class ImageUtils {

    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);


//    public static boolean ImageCheck(File file){
//        MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
//        // 不添加下面的类型会造成误判 详见：http://stackoverflow.com/questions/4855627/java-mimetypesfiletypemap-always-returning-application-octet-stream-on-android-e*/
//        mtftp.addMimeTypes("image png tif jpg jpeg bmp");
//        String mimeType= mtftp.getContentType(file);
//        String type = mimeType.split("/")[0];
//        return "image".equals(type);
//    }

    /**
     * 判断文件后缀是否为图片文件格式,bmp|gif|jpg|jpeg|png 返回true
     * @param imageFileSuffix 图片文件后缀名
     * @return
     */
    public static boolean isImageBySuffix(String imageFileSuffix) {
        if (StringUtils.isNotEmpty(imageFileSuffix)) {
            //[JPG, jpg, bmp, BMP, gif, GIF, WBMP, png, PNG, wbmp, jpeg, JPEG]
            String[] formatNames = ImageIO.getReaderFormatNames();
            if (ArrayUtils.isNotEmpty(formatNames)) {
                for (String formatName : formatNames) {
                    if (imageFileSuffix.toLowerCase().equals(formatName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 拼接图片宽长比字符串 height x width
     * @param previewImageWidth
     * @param previewImageHeight
     * @author linxin
     * @return java.lang.String
     * @date 2021/3/25 10:02
     */
    public static String genImageSizeStr(float previewImageWidth, float previewImageHeight) {
        return new StringBuffer(String.valueOf(previewImageHeight)).append("x").append(previewImageWidth).toString();
    }
//    /**
//     * 生成缩略图
//     * @param image
//     * @param outPath
//     * @param width
//     * @param height
//     * @param quality
//     * @author linxin
//     * @return void
//     * @date 2021/3/12 15:41
//     */
//    public static void generateThumbImage(File image, Path outPath, int width, int height, float quality){
//        try {
//            Thumbnails.of(image)
//                    .size(width, height)
//                    .outputQuality(quality)
//                    .toFile(outPath.toFile());
//        } catch (IOException e) {
//            log.error("生成图片缩略图异常：", e);
//        }
//    }

    /**
     * 合并任数量的图片成一张图片
     *
     * @param isHorizontal true代表水平合并，fasle代表垂直合并
     * @param imgs         待合并的图片数组
     * @return
     * @throws IOException
     */
    public static BufferedImage mergeImage(boolean isHorizontal, List<BufferedImage> imgs) throws IOException {
        // 生成新图片
        BufferedImage destImage;
        // 计算新图片的长和高
        int allw = 0, allh = 0, allwMax = 0, allhMax = 0;
        if(imgs != null && imgs.size() > 0){
            // 获取总长、总宽、最长、最宽
            imgs = imgs.stream().filter(e -> Objects.nonNull(e)).collect(Collectors.toList());
            for (int i = 0; i < imgs.size(); i++) {
                BufferedImage img = imgs.get(i);
                allw += img.getWidth();
                if (imgs.size() != i + 1) {
                    allh += img.getHeight() + 5;
                } else {
                    allh += img.getHeight();
                }
                if (img.getWidth() > allwMax) {
                    allwMax = img.getWidth();
                }
                if (img.getHeight() > allhMax) {
                    allhMax = img.getHeight();
                }
            }
            // 创建新图片
            if (isHorizontal) {
                // 横向（水平）合并 生成一张宽度为总宽度 高度为最大高度的图片
                destImage = new BufferedImage(allw, allhMax, BufferedImage.TYPE_INT_RGB);
            } else {
                // 纵向（垂直）合并 生成一张宽度为最大宽度 高度为总高度的图片
                destImage = new BufferedImage(allwMax, allh, BufferedImage.TYPE_INT_RGB);
            }
            Graphics2D g2 = (Graphics2D) destImage.getGraphics();
            g2.setBackground(Color.LIGHT_GRAY);
            // 清除矩形
            g2.clearRect(0, 0, allw, allh);
            //
            g2.setPaint(Color.RED);
            // 合并所有子图片到新图片
            int wx = 0, wy = 0;
            for (int i = 0; i < imgs.size(); i++) {
                BufferedImage img = imgs.get(i);
                int w1 = img.getWidth();
                int h1 = img.getHeight();
                // 从图片中读取RGB
                int[] imageArrayOne = new int[w1 * h1];
                // 逐行扫描图像中各个像素的RGB到数组中
                imageArrayOne = img.getRGB(0, 0, w1, h1, imageArrayOne, 0, w1);
                if (isHorizontal) {
                    // 水平方向合并
                    // 设置上半部分或左半部分的RGB
                    destImage.setRGB(wx, 0, w1, h1, imageArrayOne, 0, w1);
                } else {
                    // 垂直方向合并
                    // 设置上半部分或左半部分的RGB
                    destImage.setRGB(0, wy, w1, h1, imageArrayOne, 0, w1);
                }
                wx += w1;
                wy += h1 + 5;
            }
            return destImage;
        }
        return null;
    }


    private List<String> imageBase64EncodeStr(List<Path> fileList) {
        List<String> base64Str = new ArrayList<>(fileList.size());
        if (fileList == null || fileList.isEmpty()) {
            return null;
        }
        for (Path path : fileList) {
            try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                base64Str.add(new String(Base64Encoder.encode(bytes)));
                return base64Str;
            } catch (IOException e) {
                log.error("图片转 Base64 IO 异常：{}", e);
                return null;
            }
        }

        return null;
    }

}
