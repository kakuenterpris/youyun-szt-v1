package com.ustack.meeting.constants;

/**
 * Author：PingY
 * Package：com.ustack.meeting.constants
 * Project：intelligent-office-platform
 * Classname：AudioConstants
 * Date：2025/3/25  15:01
 * Description: 语音转写常量类
 */
public class AudioConstants {

    public static final String PROGRESS_REDIS_KEY = "MEETING_AUDIO_TRANSFORM:";

    public static final long EXPIRE_DATE_30_MIN = 30 * 60;// 过期时间30分钟

    public static final String XF_RESULT_DATA_ONEBEST = "onebest";
    public static final String XF_RESULT_DATA_BG = "bg";
    public static final String XF_RESULT_DATA_ED = "ed";
    public static final String XF_RESULT_DATA_SPEAKER = "speaker";

    public static final String FILE_ID = "fileId";
    public static final String PROGRESS_NUM = "progress";
    public static final String REAL_DURATION_STRING = "realDurationString";

    public static final int TRANS_SUCCESS = 1;
    public static final int TRANS_FAIL = 0;

}
