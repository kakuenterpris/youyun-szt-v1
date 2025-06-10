package com.ustack.file.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具
 *
 * @author linxin
 */
@Slf4j
public class FileUtil {

    /**
     * 默认缓冲数组大小
     */
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 保持结构
     */
    private static final boolean KEEP_STRUCTURE = true;

    /**
     * 保留空文件夹
     */
    private static final boolean KEEP_EMPTY_FOLDER = true;

    /**
     * 默认编码格式
     */
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    /**
     * 兼容性编码格式
     */
    private static final Charset COMPATIBLE_ENCODING = Charset.forName("GBK");

    // 支持 only office 在线编辑的文件后缀
    public static Predicate<String> FILE_EDITABLE = (suffix) -> (FileTypeEnum.DOC.getSuffix().equals(suffix)
            || FileTypeEnum.DOCX.getSuffix().equals(suffix)
            || FileTypeEnum.TXT.getSuffix().equals(suffix.toLowerCase()))
            || FileTypeEnum.PDF.getSuffix().equals(suffix)
            || FileTypeEnum.XLSX.getSuffix().equals(suffix)
            || FileTypeEnum.PPT.getSuffix().equals(suffix)
            || FileTypeEnum.PPTX.getSuffix().equals(suffix)
            || FileTypeEnum.XLS.getSuffix().equals(suffix);

    // 支持生成预览图的文件格式
    public static Predicate<String> FILE_CAN_PREVIEW = (suffix) -> (FileTypeEnum.XLS.getSuffix().equals(suffix)
            || FileTypeEnum.XLSX.getSuffix().equals(suffix)
            || FileTypeEnum.DOC.getSuffix().equals(suffix)
            || FileTypeEnum.DOCX.getSuffix().equals(suffix)
            || FileTypeEnum.PDF.getSuffix().equals(suffix)
            || FileTypeEnum.PPT.getSuffix().equals(suffix)
            || FileTypeEnum.PPTX.getSuffix().equals(suffix))
            || ImageUtils.isImageBySuffix(suffix);

    public static Predicate<String> FILE_CAN_CONVERSION_TO_PDF = (suffix) -> FileTypeEnum.DOC.getSuffix().equals(suffix)
            || FileTypeEnum.DOCX.getSuffix().equals(suffix)
            || FileTypeEnum.PPT.getSuffix().equals(suffix)
            || FileTypeEnum.PPTX.getSuffix().equals(suffix)
            || FileTypeEnum.XLS.getSuffix().equals(suffix)
            || FileTypeEnum.PDF.getSuffix().equals(suffix)
            || FileTypeEnum.XLSX.getSuffix().equals(suffix);

    private static final BigDecimal MEM_UNIT = new BigDecimal("1024");

    /**
     * 文件字节大小转成BigDecimal
     * @param fileBytes
     * @author linxin
     * @return java.math.BigDecimal
     * @date 2022/7/6 10:16
     */
    public static BigDecimal getFileSize(Long fileBytes){
        // 字节数
        return new BigDecimal(fileBytes.toString()).divide(MEM_UNIT);
    }

    /**
     * 创建所有必须但不存在的父文件夹
     *
     * @param parentFolderPath 父文件夹路径
     * @throws IOException IO异常
     */
    public static void createParentFolder(String parentFolderPath) throws IOException {
        createParentFolder(new File(parentFolderPath));
    }

    /**
     * 创建所有必须但不存在的父文件夹
     *
     * @param parentFolder 父文件夹
     * @throws IOException IO异常：创建父文件夹（父目录）失败
     */
    public static void createParentFolder(File parentFolder) throws IOException {
        if (!parentFolder.exists()) {
            if (!parentFolder.mkdirs()) {
                throw new IOException("创建父文件夹（父目录）“" + parentFolder.getPath() + "”失败");
            }
        }
    }

    public static String getFileMD5(byte[] bytes){
        MessageDigest digest = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            try ( ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes)) {
                while ((len = arrayInputStream.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 计算文件的md5值
     * @param file 需要计算md5的文件
     * @author linxin
     * @return java.lang.String
     * @date 2021/3/15 11:01
     */
    public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            try (FileInputStream fileInputStream  = new FileInputStream(file)) {
                while ((len = fileInputStream.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 文件转字节数组
     *
     * @param file 文件
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] fileToByte(File file) throws IOException {
        return fileToByte(file, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 文件转字节数组
     *
     * @param file       文件
     * @param bufferSize 缓冲区大小
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] fileToByte(File file, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        return fileToByte(file, new byte[bufferSize]);
    }

    /**
     * 文件转字节数组
     *
     * @param file   文件
     * @param buffer 缓冲区
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] fileToByte(File file, byte[] buffer) throws IOException {
        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @throws IOException IO异常
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        copyFile(sourceFile, targetFile, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @param bufferSize 缓冲区大小
     * @throws IOException IO异常
     */
    public static void copyFile(File sourceFile, File targetFile, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        copyFile(sourceFile, targetFile, new byte[bufferSize]);
    }

    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @param buffer     缓冲区
     * @throws IOException IO异常
     */
    public static void copyFile(File sourceFile, File targetFile, byte[] buffer) throws IOException {
        try (
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                FileOutputStream fileOutputStream = new FileOutputStream(targetFile)
        ) {
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
        }
    }

    /**
     * 批量拷贝
     * @param sourceDir 源文件夹
     * @param targetDir 目标文件夹
     * @author linxin
     * @return void
     * @date 2022/10/13 16:48
     */
    public static void batchCopyFile(File sourceDir, File targetDir) throws IOException {
        if (sourceDir.isDirectory()) {
            File[] files = sourceDir.listFiles();
            for (File file : files) {
                batchCopyFile(file,  targetDir);
            }
            return;
        }
        // 复制文件
        if (sourceDir.isFile()) {
            // 快速复制
            copyFileToDirNio(sourceDir, targetDir.getPath());
        }
    }

    /**
     * 快速复制文件
     * @param src 源文件
     * @param target 目标路径
     */
    public static void copyFileToDirNio(File src, String target) throws IOException {
            File targetPath = new File(target);
            // 判断文件是否存在
            if (!targetPath.exists()) {
                targetPath.mkdirs();
            }
            File targetFile = new File(targetPath + File.separator + src.getName());
            // try resource 自动关闭流
            try (FileInputStream fis = new FileInputStream(src);
                 FileOutputStream fos = new FileOutputStream(targetFile);
                 FileChannel inputChannel = fis.getChannel();
                 FileChannel outputChannel = fos.getChannel()) {
                // 将源文件数据通过通道传输到目标文件
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            }
    }


    public static void copyFileNio(File sourceFile, File targetFile) throws IOException {
        try (FileChannel src = FileChannel.open(sourceFile.toPath(), StandardOpenOption.READ);
            FileChannel target = FileChannel.open(targetFile.toPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
        ) {
            src.transferTo(0, src.size(), target);
        }
    }

    public static void copyFileNio(Path sourcePath, Path targetPath) throws IOException {
        try (FileChannel src = FileChannel.open(sourcePath, StandardOpenOption.READ);
             FileChannel target = FileChannel.open(targetPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
        ) {
            src.transferTo(0, src.size(), target);
        }
    }

    /**
     * 复制文件夹
     *
     * @param sourceFolder 源文件夹
     * @param targetFolder 目标文件夹
     * @throws IOException IO异常
     */
    public static void copyFolder(File sourceFolder, File targetFolder) throws IOException {
        copyFolder(sourceFolder, targetFolder, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 复制文件夹
     *
     * @param sourceFolder 源文件夹
     * @param targetFolder 目标文件夹
     * @param bufferSize   缓冲区大小
     * @throws IOException IO异常
     */
    public static void copyFolder(File sourceFolder, File targetFolder, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        copyFolder(sourceFolder, targetFolder, new byte[bufferSize]);
    }

    /**
     * 复制文件夹
     *
     * @param sourceFolder 源文件夹
     * @param targetFolder 目标文件夹
     * @param buffer       缓冲区
     * @throws IOException IO异常
     */
    public static void copyFolder(File sourceFolder, File targetFolder, byte[] buffer) throws IOException {
        copyFolderContent(sourceFolder, new File(targetFolder.getPath(), sourceFolder.getName()), buffer);
    }

    /**
     * 复制文件夹内容
     *
     * @param sourceFolder 源文件夹
     * @param targetFolder 目标文件夹
     * @throws IOException IO异常
     */
    public static void copyFolderContent(File sourceFolder, File targetFolder) throws IOException {
        copyFolderContent(sourceFolder, targetFolder, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 复制文件夹内容
     *
     * @param sourceFolder 源文件夹
     * @param targetFolder 目标文件夹
     * @param bufferSize   缓冲区大小
     * @throws IOException IO异常
     */
    public static void copyFolderContent(File sourceFolder, File targetFolder, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        copyFolderContent(sourceFolder, targetFolder, new byte[bufferSize]);
    }

    /**
     * 复制文件夹内容
     *
     * @param sourceFolder 源文件夹
     * @param targetFolder 目标文件夹
     * @param buffer       缓冲区
     * @throws IOException IO异常
     */
    public static void copyFolderContent(File sourceFolder, File targetFolder, byte[] buffer) throws IOException {
        if (sourceFolder.isDirectory()) {
            createParentFolder(targetFolder);
            File[] files = sourceFolder.listFiles();
            if (files != null && files.length > 0) {
                String sourceFolderPath = sourceFolder.getPath();
                String targetFolderPath = targetFolder.getPath();
                String fileName;
                for (File file : files) {
                    fileName = file.getName();
                    copyFolderContent(new File(sourceFolderPath, fileName), new File(targetFolderPath, fileName), buffer);
                }
            }
        } else {
            copyFile(sourceFolder, targetFolder, buffer);
        }
    }

    /**
     * 压缩文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @throws IOException IO异常
     */
    public static void compressFile(File sourceFile, File targetFile) throws IOException {
        compressFile(sourceFile, targetFile, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 压缩文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @param bufferSize 缓冲区大小
     * @throws IOException IO异常
     */
    public static void compressFile(File sourceFile, File targetFile, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        compressFile(sourceFile, targetFile, new byte[bufferSize]);
    }

    /**
     * 压缩文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @param buffer     缓冲区
     * @throws IOException IO异常
     */
    public static void compressFile(File sourceFile, File targetFile, byte[] buffer) throws IOException {
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
                ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream, DEFAULT_ENCODING)
        ) {
            compressFile(sourceFile, sourceFile.getName(), buffer, zipOutputStream);
        }
    }

    /**
     * 压缩文件
     *
     * @param sourceFile      源文件
     * @param fileName        文件名
     * @param zipOutputStream Zip输出流
     * @throws IOException IO异常
     */
    public static void compressFile(File sourceFile, String fileName, ZipOutputStream zipOutputStream) throws IOException {
        compressFile(sourceFile, fileName, new byte[DEFAULT_BUFFER_SIZE], zipOutputStream);
    }

    /**
     * 压缩文件
     *
     * @param sourceFile      源文件
     * @param fileName        文件名
     * @param buffer          缓冲区
     * @param zipOutputStream Zip输出流
     * @throws IOException IO异常
     */
    public static void compressFile(File sourceFile, String fileName, byte[] buffer, ZipOutputStream zipOutputStream) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(sourceFile)) {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, length);
            }
            zipOutputStream.flush();
        }
    }

    /**
     * 压缩文件夹
     *
     * @param sourceFolder 源文件夹
     * @param targetFile   目标文件
     * @throws IOException IO异常
     */
    public static void compressFolder(File sourceFolder, File targetFile) throws IOException {
        compressFolder(sourceFolder, targetFile, new byte[DEFAULT_BUFFER_SIZE], KEEP_STRUCTURE, KEEP_EMPTY_FOLDER);
    }

    /**
     * 压缩文件夹
     *
     * @param sourceFolder    源文件夹
     * @param targetFile      目标文件夹
     * @param bufferSize      缓冲区大小
     * @param keepStructure   保持结构（如不保持结构，则所有文件、文件夹均在压缩包的根目录下）
     * @param keepEmptyFolder 保留空文件夹
     * @throws IOException IO异常
     */
    public static void compressFolder(File sourceFolder, File targetFile, int bufferSize, boolean keepStructure, boolean keepEmptyFolder) throws IOException {
        compressFolder(sourceFolder, targetFile, new byte[bufferSize], keepStructure, keepEmptyFolder);
    }

    /**
     * 压缩文件夹
     *
     * @param sourceFolder    源文件
     * @param targetFile      目标文件
     * @param buffer          缓冲区
     * @param keepStructure   保持结构（如不保持结构，则所有文件、文件夹均在压缩包的根目录下）
     * @param keepEmptyFolder 保留空文件夹
     * @throws IOException IO异常
     */
    public static void compressFolder(File sourceFolder, File targetFile, byte[] buffer, boolean keepStructure, boolean keepEmptyFolder) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            compressFolder(sourceFolder, "", buffer, zipOutputStream, keepStructure, keepEmptyFolder);
            zipOutputStream.close();
        }
    }

    /**
     * 压缩文件夹
     *
     * @param sourceFolder    源文件
     * @param fileName        文件名
     * @param zipOutputStream Zip输出流
     * @throws IOException IO异常
     */
    public static void compressFolder(File sourceFolder, String fileName, ZipOutputStream zipOutputStream) throws IOException {
        compressFolder(sourceFolder, fileName, new byte[DEFAULT_BUFFER_SIZE], zipOutputStream, KEEP_STRUCTURE, KEEP_EMPTY_FOLDER);
    }

    /**
     * 压缩文件夹
     *
     * @param sourceFolder    源文件
     * @param fileName        文件名
     * @param buffer          缓冲区
     * @param zipOutputStream Zip输出流
     * @throws IOException IO异常
     */
    public static void compressFolder(File sourceFolder, String fileName, byte[] buffer, ZipOutputStream zipOutputStream) throws IOException {
        compressFolder(sourceFolder, fileName, buffer, zipOutputStream, KEEP_STRUCTURE, KEEP_EMPTY_FOLDER);
    }

    /**
     * 压缩文件夹
     *
     * @param sourceFolder    源文件
     * @param fileName        文件名
     * @param buffer          缓冲区
     * @param zipOutputStream Zip输出流
     * @param keepStructure   保持结构（如不保持结构，则所有文件、文件夹均在压缩包的根目录下）
     * @param keepEmptyFolder 保留空文件夹
     * @throws IOException IO异常
     */
    public static void compressFolder(File sourceFolder, String fileName, byte[] buffer, ZipOutputStream zipOutputStream, boolean keepStructure, boolean keepEmptyFolder) throws IOException {
        if (sourceFolder.isDirectory()) {
            if (keepEmptyFolder) {
                zipOutputStream.putNextEntry(new ZipEntry(fileName + "/"));
            }
            File[] files = sourceFolder.listFiles();
            if (files != null && files.length > 0) {
                String currentFileName;
                String newFileName;
                for (File file : files) {
                    currentFileName = file.getName();
                    newFileName = fileName;
                    if (keepStructure) {
                        newFileName = newFileName + File.separator + currentFileName;
                    }
                    compressFolder(new File(sourceFolder.getPath(), currentFileName), newFileName, buffer, zipOutputStream, keepStructure, keepEmptyFolder);
                }
            }
        } else {
            compressFile(sourceFolder, fileName, buffer, zipOutputStream);
        }
    }

    /**
     * 解压
     *
     * @param sourceCompressFile 源压缩文件
     * @param targetFolder       目标文件夹
     * @throws IOException IO异常
     */
    public static void decompress(File sourceCompressFile, File targetFolder) throws IOException {
        decompress(sourceCompressFile, targetFolder, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 解压缩
     *
     * @param sourceCompressFile 源压缩文件
     * @param targetFolder       目标文件夹
     * @param bufferSize         缓冲区大小
     * @throws IOException IO异常
     */
    public static void decompress(File sourceCompressFile, File targetFolder, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        decompress(sourceCompressFile, targetFolder, new byte[bufferSize]);
    }

    /**
     * 解压缩
     *
     * @param sourceCompressFile 源压缩文件
     * @param targetFolder       目标文件夹
     * @param buffer             缓冲区
     * @throws IOException IO异常
     */
    public static void decompress(File sourceCompressFile, File targetFolder, byte[] buffer) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(sourceCompressFile);
             ZipInputStream zipInputStream = new ZipInputStream(fileInputStream, COMPATIBLE_ENCODING)) {
            createParentFolder(targetFolder);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.getName().endsWith("/") || zipEntry.getName().endsWith("\\")) {
                    File newFolder = new File(targetFolder.getPath(), zipEntry.getName());
                    createParentFolder(newFolder);
                } else {
                    decompress(new File(targetFolder.getPath(), zipEntry.getName()), buffer, zipInputStream);
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    /**
     * 解压缩
     *
     * @param targetFile     目标文件
     * @param buffer         缓冲区
     * @param zipInputStream Zip输入流
     * @throws IOException IO异常
     */
    public static void decompress(File targetFile, byte[] buffer, ZipInputStream zipInputStream) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            int length;
            while ((length = zipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
        }
    }


    /**
     * 文件删除
     *
     * @param file    要删除的文件或者文件夹，文件夹会递归删除
     * @return void
     * @author linxin
     * @date 2021/3/30 08:37
     */
    public static void deleteFile(File file) {
        // 判断是否为文件夹
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
            file.delete();
        }else {
            file.delete();
        }

    }

    public static void deleteFiles(List<Path> files){
        for (int i = 0; i < files.size(); i++) {
            deleteFile(files.get(i).toFile());
        }
    }

    /**
     * @param dir          搜索目录
     * @param key          搜索的关键字
     * @param searchHidden 是否搜索隐藏文件，false不搜索
     */
    public static List<File> searchFile(String dir, String key, boolean searchHidden, List<File> files) {
        File file = new File(dir);
        File[] fileList = file.listFiles();
        String fileName = "";
        String filePath = "";
        if (fileList == null || fileList.length == 0) {
            return null;
        }
        for (File f : fileList) {
            // 不搜索隐藏文件
            if (!searchHidden && f.isHidden()) {
                continue;
            }
            fileName = f.getName();
            filePath = f.getPath();
            if (f.isFile()) {
                // 获取文件名忽略后缀
                String fileNameIgnoreSuffix = fileName;
                if (fileNameIgnoreSuffix.toLowerCase().contains(key.trim().toLowerCase())) {
                    files.add(f);
                }
            } else if (f.isDirectory()) {
                searchFile(filePath, key, searchHidden, files);
            }
        }
        return files;
    }

    public enum ImageTypeEnum{

        PNG("png"),
        JPG("jpg"),
        BMP("bmp"),
        GIF("gif"),
        WBMP("wbmp"),
        JPEG("jpeg");

        @Getter
        private final String suffix;

        ImageTypeEnum(String suffix) {
            this.suffix = suffix;
        }


        public static ImageTypeEnum getInstance(String suffix){
            final Optional<ImageTypeEnum> first = Arrays.stream(ImageTypeEnum.values())
                    .filter(e -> suffix.equals(e.getSuffix())).findFirst();
            if(first.isPresent()){
                return first.get();
            }else {
                throw new RuntimeException(String.format("文件后缀 %s 不存在枚举类中", suffix));
            }
        }
    }

    public enum FileTypeEnum {

        PNG("png"),
        JPG("jpg"),
        BMP("bmp"),
        GIF("gif"),
        WBMP("wbmp"),
        JPEG("jpeg"),
        PDF("pdf"),
        DOC("doc"),
        DOCX("docx"),
        XLS("xls"),
        XLSX("xlsx"),
        PPT("ppt"),
        PPTX("pptx"),
        ZIP("zip"),
        TXT("txt");

        @Getter
        private final String suffix;

        FileTypeEnum(String suffix) {
            this.suffix = suffix;
        }


        public static FileTypeEnum getInstance(String suffix){
            final Optional<FileTypeEnum> first = Arrays.stream(FileTypeEnum.values())
                    .filter(e -> suffix.equals(e.getSuffix())).findFirst();
            if(first.isPresent()){
                return first.get();
            }else {
                throw new RuntimeException(String.format("文件后缀 %s 不存在枚举类中", suffix));
            }
        }
    }

    public static final String TEMP_FILE_PREFIX = "TEMP_FILE_";

}

