package com.tsinghua.course.Biz.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tsinghua.course.Base.Constant.KeyConstant;
import com.tsinghua.course.Biz.BizTypeEnum;
import com.tsinghua.course.Base.Error.CourseWarn;
import com.tsinghua.course.Base.Error.SystemErrorEnum;
import com.tsinghua.course.Base.Error.UserWarnEnum;
import com.tsinghua.course.Biz.Controller.Params.CommonInParams;
import com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out.SysErrorOutParams;
import com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out.SysWarnOutParams;
import com.tsinghua.course.Biz.Dispatcher;
import com.tsinghua.course.Frame.Util.LogUtil;
import com.tsinghua.course.Frame.Util.SocketUtil;
import com.tsinghua.course.Frame.Util.ThreadUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @描述 处理WebSocket长连接的类，每次收到WebSocket消息就会调用channelRead0函数
 **/
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    @Autowired
    Dispatcher dispatcher;

    /** 处理WebSocket连接的实际函数 */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        BizTypeEnum bizTypeEnum = null;
        JSONObject jsonMsg = new JSONObject();
        String username = null;
        String retStr = new SysErrorOutParams().toString();
        try {
            /** WebSocket只处理文本型消息 */
            if (!(msg instanceof TextWebSocketFrame)) {
                throw new CourseWarn(SystemErrorEnum.PARAMS_ERROR);
            }
            /** 获取参数和操作类型 */
            try {
                jsonMsg = JSON.parseObject(((TextWebSocketFrame)msg).text());
                String bizTypeStr = jsonMsg.getString(KeyConstant.BIZ_TYPE);
                bizTypeEnum = BizTypeEnum.valueOf(bizTypeStr);
                jsonMsg.put(KeyConstant.BIZ_TYPE, bizTypeEnum);
            } catch (Exception e) {
                throw new CourseWarn(SystemErrorEnum.PARAMS_TRANSFER_ERROR);
            }
            /** 执行业务的线程存在复用情况，需要清除以前的线程变量 */
            ThreadUtil.clean();
            /** 保存管道，方便以后对该用户发送消息 */
            ThreadUtil.setCtx(ctx);
            /** 根据操作类型获取参数 */
            Class<CommonInParams> clz = dispatcher.getParamByBizType(bizTypeEnum);
            CommonInParams params = clz.newInstance();
            params.fromJsonObject(jsonMsg);
            /** 如果不是登录，需要获取用户信息 */
            if (!bizTypeEnum.equals(BizTypeEnum.USER_LOGIN)) {
                username = SocketUtil.getSocketUser(ctx);
                params.setUsername(username);
            } else {
                username = params.getUsername();
                if (username == null)
                    throw new CourseWarn(UserWarnEnum.LOGIN_FAILED);
            }
            /** 执行业务 */
            retStr = dispatcher.dispatch(params);
        } catch (Exception e) {
            /** 处理出错，记录警告日志或者错误日志 */
            if (e instanceof CourseWarn) {
                CourseWarn warn = (CourseWarn)e;
                retStr = new SysWarnOutParams(warn).toString();
                LogUtil.WARN(username, bizTypeEnum, jsonMsg.toString(), warn);
            } else {
                retStr = new SysErrorOutParams().toString();
                LogUtil.ERROR(username, bizTypeEnum, jsonMsg.toString(), e);
            }
        } finally {
            ctx.channel().write(new TextWebSocketFrame(retStr));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
