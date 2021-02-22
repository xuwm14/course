package com.tsinghua.course.Biz.Processor;

import com.tsinghua.course.Base.Constant.KeyConstant;
import com.tsinghua.course.Base.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @描述 测试用的原子处理器，书写正式代码时可以删除
 **/
@Component
public class TestProcessor {
    @Autowired
    MongoTemplate mongoTemplate;

    /** 向用户 admin 的testArr中增加时间字符串 */
    public void addTimeStr() {
        /** 查询模块 */
        Query query = new Query();
        query.addCriteria(Criteria.where(KeyConstant.USERNAME).is("admin"));
        /** 更新模块 */
        Update update = new Update();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        update.push("testArr", simpleDateFormat.format(new Date()));
        /** 在User集合中更新第一个查找到的元素 */
        mongoTemplate.updateFirst(query, update, User.class);
    }

    /** 设置用户 test 的testObj和subObj中的时间属性 */
    public void setTimeAttr() {
        /** 查询模块 */
        Query query = new Query();
        query.addCriteria(Criteria.where(KeyConstant.USERNAME).is("test"));
        /** 更新模块 */
        Update update = new Update();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = simpleDateFormat.format(new Date());
        update.set("testObj.time", timeStr);
        update.set("subObj.time", timeStr);
        /** 在User集合中更新第一个查找到的元素 */
        mongoTemplate.updateFirst(query, update, User.class);
    }
}
