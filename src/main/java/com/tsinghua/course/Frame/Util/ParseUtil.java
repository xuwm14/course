package com.tsinghua.course.Frame.Util;

import com.alibaba.fastjson.JSONObject;
import com.tsinghua.course.Base.Constant.GlobalConstant;
import com.tsinghua.course.Base.Constant.NameConstant;
import com.tsinghua.course.Base.Error.CourseWarn;
import io.netty.handler.codec.http.multipart.FileUpload;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @描述 解析组件，实现各种各样的解析功能
 **/
public class ParseUtil {

    /** 警告日志不重要，只需要记录一层堆栈信息 */
    public static String getWarnStackInfo(CourseWarn courseWarn) {
        if (courseWarn == null)
            return null;
        String stackInfo = null;
        StackTraceElement[] stackTrace = courseWarn.getStackTrace();
        for (StackTraceElement stackTraceElement:stackTrace) {
            String className = stackTraceElement.getClassName();
            /** 优先记录Controller层的堆栈信息 */
            if (className.startsWith(NameConstant.PACKAGE_NAME + ".Biz.Controller")) {
                stackInfo = stackTraceElement.getClassName() + GlobalConstant.ARG_SPLIT
                        + stackTraceElement.getMethodName() + GlobalConstant.ARG_SPLIT
                        + stackTraceElement.getLineNumber() + GlobalConstant.STACK_SPLIT;
                break;
            }
        }
        if (stackInfo == null) {
            /** 如果没有controller层堆栈信息则记录其它层的信息 */
            for (StackTraceElement stackTraceElement:stackTrace) {
                String className = stackTraceElement.getClassName();
                if (className.startsWith(NameConstant.PACKAGE_NAME)) {
                    stackInfo = stackTraceElement.getClassName() + GlobalConstant.ARG_SPLIT
                            + stackTraceElement.getMethodName() + GlobalConstant.ARG_SPLIT
                            + stackTraceElement.getLineNumber() + GlobalConstant.STACK_SPLIT;
                    break;
                }
            }
        }
        return stackInfo;
    }

    /** 错误日志很重要，需要记录所有堆栈信息 */
    public static String getErrorStackInfo(Throwable recordError) {
        if (recordError == null)
            return null;
        StringBuilder stackInfo = new StringBuilder();
        StackTraceElement[] stackTrace = recordError.getStackTrace();
        for (StackTraceElement stackTraceElement:stackTrace) {
            String className = stackTraceElement.getClassName();
            if (className.startsWith(NameConstant.PACKAGE_NAME)) {
                stackInfo.append(stackTraceElement.getClassName())
                        .append(GlobalConstant.ARG_SPLIT)
                        .append(stackTraceElement.getMethodName())
                        .append(GlobalConstant.ARG_SPLIT)
                        .append(stackTraceElement.getLineNumber())
                        .append(GlobalConstant.STACK_SPLIT);
            }
        }
        return stackInfo.toString();
    }

    /** 部分jsonObject不可以转化为字符串，需要特殊处理 */
    public static String getJSONString(JSONObject jsonObject) {
        for (String key:jsonObject.keySet()) {
            Object val = jsonObject.get(key);
            if (val == null)
                continue;
            if (val instanceof FileUpload) {
                /** 文件需要记录文件名称 */
                FileUpload fileUpload = (FileUpload)val;
                jsonObject.put(key, "file-" + fileUpload.getFilename());
            } else {
                /** 否则直接放入json对象中 */
                jsonObject.put(key, val);
            }
        }
        return jsonObject.toString();
    }

    /** 获取类中定义的所有内部变量 */
    public static Field[] getAllFields(final Class<?> cls) {
        final List<Field> allFieldsList = getAllFieldsList(cls);
        return allFieldsList.toArray(new Field[allFieldsList.size()]);
    }

    /** 获取类中定义的所有内部变量 */
    public static List<Field> getAllFieldsList(final Class<?> cls) {
        final List<Field> allFields = new ArrayList<Field>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                allFields.add(field);
            }
            /** 循环获取父类定义的内部变量 */
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }
}
