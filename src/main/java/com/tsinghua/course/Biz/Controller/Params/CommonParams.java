package com.tsinghua.course.Biz.Controller.Params;

import com.alibaba.fastjson.JSONObject;
import com.tsinghua.course.Frame.Util.ParseUtil;

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
    public void fromJsonObject(JSONObject paramJson) throws IllegalAccessException {
        /** 根据json中对应属性的键值来设置属性值 */
        Field[] fields = ParseUtil.getAllFields(getClass());
        for (Field field : fields) {
            if (paramJson.containsKey(field.getName())) {
                field.setAccessible(true);
                field.set(this, paramJson.get(field.getName()));
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
