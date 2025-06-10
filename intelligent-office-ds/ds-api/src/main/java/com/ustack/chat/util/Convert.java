package com.ustack.chat.util;

/**
 * 类型转换器
 *
 */
public class Convert {

    /**
     * 转换为字符串<br>
     * 如果给定的值为null，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static String toStr(Object value, String defaultValue)
    {
        if (null == value)
        {
            return defaultValue;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * 转换为字符串<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static String toStr(Object value)
    {
        return toStr(value, null);
    }

}
