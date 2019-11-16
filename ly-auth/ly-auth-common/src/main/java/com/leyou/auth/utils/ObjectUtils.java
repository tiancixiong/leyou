package com.leyou.auth.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: TianCi.Xiong
 * @Description: 从jwt解析得到的数据是Object类型，转换为具体类型可能出现空指针，这个工具类进行了一些转换
 * @Date: Created in 2019-11-14 9:35
 */
public class ObjectUtils {
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public static Long toLong(Object obj) {
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof Double || obj instanceof Float) {
            return Long.valueOf(StringUtils.substringBefore(obj.toString(), "."));
        }
        if (obj instanceof Number) {
            return Long.valueOf(obj.toString());
        }
        if (obj instanceof String) {
            return Long.valueOf(obj.toString());
        } else {
            return 0L;
        }
    }

    public static Integer toInt(Object obj) {
        return toLong(obj).intValue();
    }
}
