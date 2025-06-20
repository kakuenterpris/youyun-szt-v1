package com.ustack.chat.util;


import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 错误信息处理类。
 *
 */
public class ExceptionUtil {

    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

}
