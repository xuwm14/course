package com.tsinghua.course.Biz.Controller.Params;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tsinghua.course.Base.Annotation.Required;
import com.tsinghua.course.Base.Error.CourseWarn;
import com.tsinghua.course.Frame.Util.ParseUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * @描述 通用参数，主要是封装一些通用操作
 **/
public abstract class CommonParams {
    /** 在转换为jsonObject对象前进行的操作 */
    protected abstract void beforeTransfer();

    /** 转换为jsonObject对象 */
    public JSONObject toJsonObject() throws Exception {
        beforeTransfer();
        JSONObject jsonObject = new JSONObject();
        /** 反射获取所有的内部属性，然后设置到json中 */
        Field[] fields = ParseUtil.getAllFields(getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            jsonObject.put(field.getName(), field.get(this));
        }
        return jsonObject;
    }

    /** 嵌套JSON数组转Java默认数组 */
    public static Object transferJsonArr(JSONArray srcArr, Class type) {
        Object fillArr = Array.newInstance(type, srcArr.size());
        for (int i = 0; i < srcArr.size(); ++i) {
            Object subObj = srcArr.get(i);
            if (srcArr.get(i) instanceof JSONArray) /** 解析嵌套数组 */
                Array.set(fillArr, i, transferJsonArr((JSONArray) subObj, type.getComponentType()));
            else {
                /** 解析最终子元素 */
                try {
                    Array.set(fillArr, i, subObj);
                } catch (Exception e) {
                    Array.set(fillArr, i, JSON.parseObject(subObj.toString(), type));
                }
            }
        }
        return fillArr;
    }

    /** 根据jsonObject对象解析参数 */
    public void fromJsonObject(JSONObject paramJson) throws Exception {
        /** 根据json中对应属性的键值来设置属性值 */
        Field[] fields = ParseUtil.getAllFields(getClass());
        for (Field field : fields) {
            if (paramJson.containsKey(field.getName())) {
                field.setAccessible(true);
                Object obj = paramJson.get(field.getName());
                /** 解析数组需要特殊处理 */
                if (field.getType().isArray()) {
                    Class subCls = field.getType().getComponentType();
                    if (obj instanceof JSONArray) {
                        JSONArray arr = (JSONArray) obj;
                        field.set(this, transferJsonArr(arr, field.getType().getComponentType()));
                    } else {
                        Object fillArr = Array.newInstance(subCls, 1);
                        Array.set(fillArr, 0, obj);
                        field.set(this, fillArr);
                    }
                } else {
                    /** 不是数组直接赋值 */
                    try {
                        field.set(this, obj);
                    } catch (Exception e) {
                        field.set(this, JSON.parseObject(obj.toString(), field.getType()));
                    }
                }
            } else if (field.isAnnotationPresent(Required.class)) {
                throw new CourseWarn("default", field.getName() + "不能为空");
            }
        }
    }

    /** 重写转换字符串操作 */
    @Override
    public String toString() {
        try {
            return ParseUtil.getJSONString(toJsonObject());
        } catch (Exception e) {
            return null;
        }
    }
}
