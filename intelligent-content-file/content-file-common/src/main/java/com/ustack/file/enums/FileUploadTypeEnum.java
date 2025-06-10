package com.ustack.file.enums;

import lombok.Getter;

import java.util.stream.Stream;

public enum FileUploadTypeEnum{
        // 文件已上传
        FILE_UPLOADED(10, "文件已上传"),
        // 文件分片部分上传
        FILE_PART_UPLOADED(20, "文件部分上传"),
        // 文件未上传
        FILE_NOT_UPLOAD_YET(30, "文件未上传"),
        // 分片已上传
        SILICE_UPLOADED(11, "文件分片已上传"),
        // 分片未上传
        SILICE_NOT_UPLOAD_YET(21, "文件分片未上传");

        @Getter
        private int code;

        @Getter
        private String desc;
        FileUploadTypeEnum(int code, String desc) {
                this.desc = desc;
                this.code = code;
        }

        public static FileUploadTypeEnum getInstance(int code) {
                final FileUploadTypeEnum typeEnum = Stream.of(FileUploadTypeEnum.values())
                        .filter(e -> e.code == code)
                        .findFirst().orElse(null);
                return typeEnum;

        }

    }