package com.ustack.file.utils;


import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * @author linxin
 */
@Slf4j
public class FileNameUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** 默认无后缀文件临时后缀*/
    public static final String default_format = "temp";

    public static final String path_regx = "^\\/(\\w+\\/?)+$";


    /**
     * 生成文件存放路径：根路径 + 年/月/日  分片文件：根路径 +  年/月/日/UUID
     * @param fileBasePath 根路径
     * @param UUID 主文件放到日期级别目录，临时分片文件和预览文件放到 日期/uuid/ 下
     * @author linxin
     * @return java.lang.String
     * @date 2021/3/15 14:47
     */
    private static String genSavePath(String fileBasePath, String UUID){
        StringBuffer stringBuffer = new StringBuffer(fileBasePath);
        LocalDate now = LocalDate.now();
        String date = now.format(formatter);
        final String[] split = date.split("-");
        String year =  split[0];
        String month = split[1].startsWith("0") ? split[1].replace("0", "") : split[1];
        String day = split[2].startsWith("0") ? split[2].replace("0", "") : split[2];
        stringBuffer.append(File.separator).append(year)
                .append(File.separator).append(month)
                .append(File.separator).append(day)
                .append(File.separator).append(UUID);
        return stringBuffer.toString();
    }

    /**
     * 数据库字段保存的路径
     * @author linxin
     * @return java.lang.String
     * @date 2021/11/30 14:01
     */
    public static String genDataBasePath(){
        StringBuffer stringBuffer = new StringBuffer();
        LocalDate now = LocalDate.now();
        String date = now.format(formatter);
        final String[] split = date.split("-");
        String year =  split[0];
        String month = split[1].startsWith("0") ? split[1].replace("0", "") : split[1];
        String day = split[2].startsWith("0") ? split[2].replace("0", "") : split[2];
        stringBuffer.append(File.separator).append(year)
                .append(File.separator).append(month)
                .append(File.separator).append(day)
                .append(File.separator);
        return stringBuffer.toString();
    }



    /**
     * 生成文件上传分片文件保存路径
     * @param fileBasePath
     * @param UUID
     * @author linxin
     * @return java.lang.String
     * @date 2021/11/30 14:06
     */
    public static String genSliceSaveDir(String fileBasePath, String UUID){
        return genSavePath(fileBasePath, UUID);
    }

    /**
     * 生成文件保存路径
     * @param fileBasePath
     * @author linxin
     * @return java.lang.String
     * @date 2021/11/30 14:08
     */
    public static String genFileSaveDir(String fileBasePath){
        return genSavePath(fileBasePath, "");
    }

    /**
     * 获取当日文件保存目录，如果目录不存在则创建
     * @param fileBasePath
     * @author linxin
     * @return java.lang.String
     * @date 2022/6/29 13:41
     */
    public static String genFileDirStrAndCreateDir(String fileBasePath) {
        String fileDirStr = FileNameUtil.genFileSaveDir(fileBasePath);
        File file = new File(fileDirStr);
        if (!file.exists()) {
            // 创建文件夹
            file.mkdirs();
        }
        return fileDirStr;
    }

    /**
     * 根据源文件名生成一个新的文件名
     * @param fileName
     * @param version
     * @author linxin
     * @return java.lang.String
     * @date 2022/6/30 14:20
     */
    public static String genNewFileName(String fileName, String version){
        // 源文件名加上版本号
        return StringUtils.join(getPrefix(fileName) , "_",  version ,  "." , getSuffix(fileName));
    }

    public static String getSuffix(String fileName){
        String s = FileUtil.getSuffix(fileName);
        return s.toLowerCase();
    }

    public static String getSuffix(File file){
        if(Objects.isNull(file)){
            return "";
        }
        return getSuffix(file.getName());
    }

    public static String getPrefix(String fileName){
        return FileUtil.getPrefix(fileName);
    }

    public static String getPrefix(File file) {
        if(Objects.isNull(file)){
            return "";
        }
        return getPrefix(file.getName());
    }
    /**
     * 生成文件真实路径 /baseDir/year/month/day/uuid.suffix
     * @param saveDirectory 文件保存目录
     * @param uuid  uuid
     * @param suffix 后缀
     * @author linxin
     * @return java.lang.String
     * @date 2021/3/19 16:29
     */
    public static String genFileSavePath(String saveDirectory, String uuid, String suffix){
        if(StringUtils.isBlank(suffix)){
            // 如果后缀为空字符串，文件名存uuid就行
            return new StringBuffer(saveDirectory).append(File.separator).append(uuid).toString();
        }
        StringBuffer pathBuffer = new StringBuffer(saveDirectory)
                .append(File.separator)
                .append(uuid)
                .append(".")
                .append(suffix);
        return pathBuffer.toString();
    }

    public static String genFileDownloadPath(String path, String fileName){
        return path + fileName;
    }

    /**
     * 生成分片文件真实路径
     * @param sliceDirStr 保存路径 /baseDir/year/month/day/uuid/
     * @param trunk 分片
     * @author linxin
     * @return java.lang.String
     * @date 2021/3/23 21:59
     */
    public static String genSlicePath(String sliceDirStr, Integer trunk){
        StringBuffer pathBuffer = new StringBuffer(sliceDirStr)
                .append(File.separator)
                .append(trunk);
        return pathBuffer.toString();
    }

    /**
     * 生成预览图片保存路径
     * @param guid 文件guid
     * @param sourceFile 生成预览图的源文件
     * @author linxin
     * @return java.lang.String
     * @date 2022/7/1 15:48
     */
    public static String getPreviewSaveDirStr(String guid, File sourceFile) {
        return sourceFile.getParent() + File.separator + guid + File.separator;
    }

    /**
     * 获取预览图保存目录
     * @param savePath 文件目录全路径
     * @param guid
     * @author linxin
     * @return java.lang.String
     * @date 2022/9/7 08:57
     */
    public static String getPreviewSaveDirStr(String savePath, String guid){
        return savePath + guid;
    }

    /**
     * 生成only office editKey
     * @author linxin
     * @return java.lang.String
     * @date 2021/3/31 11:17
     */
    public static String genEditKey(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成文件在磁盘上的名字
     * @param uuid uuid
     * @param fileExtension 文件后缀
     * @author linxin
     * @return java.lang.String
     * @date 2022/6/28 10:26
     */
    public static String genFileName(String uuid, String fileExtension) {
        return new StringBuffer(uuid).append(".").append(fileExtension).toString();
    }

    /**
     * 字符串中将 // /// 等 统一为/
     *
     * @param input 字符串
     * @return 解码结果
     */
    public static String filePathFormat(String input) {
        String out = input.replace("////", File.separator);
        out = out.replace("///", File.separator);
        out = out.replace("//", File.separator);
        return out;
    }

}
