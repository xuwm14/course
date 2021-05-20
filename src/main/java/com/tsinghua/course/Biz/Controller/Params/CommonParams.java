package com.tsinghua.course.Biz.Controller.Params;

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
                        Object fillArr = Array.newInstance(subCls, arr.size());
                        for (int i = 0; i < arr.size(); ++i)
                            Array.set(fillArr, i, arr.get(i));
                        field.set(this, fillArr);
                    } else {
                        Object fillArr = Array.newInstance(subCls, 1);
                        Array.set(fillArr, 0, obj);
                        field.set(this, fillArr);
                    }
                } else {
                    /** 不是数组直接赋值 */
                    field.set(this, obj);
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
