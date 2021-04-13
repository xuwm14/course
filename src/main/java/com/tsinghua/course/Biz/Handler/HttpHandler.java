package com.tsinghua.course.Biz.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tsinghua.course.Base.Constant.KeyConstant;
import com.tsinghua.course.Base.Constant.NameConstant;
import com.tsinghua.course.Base.Enum.BizTypeEnum;
import com.tsinghua.course.Base.Error.CourseWarn;
import com.tsinghua.course.Base.Error.SystemErrorEnum;
import com.tsinghua.course.Biz.Controller.Params.CommonInParams;
import com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out.SysErrorOutParams;
import com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out.SysWarnOutParams;
import com.tsinghua.course.Biz.Dispatcher;
import com.tsinghua.course.Frame.Util.HttpSession;
import com.tsinghua.course.Frame.Util.LogUtil;
import com.tsinghua.course.Frame.Util.ParseUtil;
import com.tsinghua.course.Frame.Util.ThreadUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @描述 处理http请求的类，系统每次收到http请求都会调用channelRead0方法
 **/
@Component
@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Autowired
    private Dispatcher dispatcher;
    /** 存储httpSession的对象 */
    private HttpSession httpSession;
    /** 之前是否已经存在httpSession */
    private boolean hasPreSession;

    /** 处理http请求的实际函数 */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        JSONObject requestParams = new JSONObject();
        BizTypeEnum bizTypeEnum = null;
        try {
            /** 执行业务的线程存在复用情况，需要清除以前的线程变量 */
            ThreadUtil.clean();
            /** 获取session，如果session，如果不存在需要新建session */
            getSession(request);
            if (!hasPreSession)
                httpSession = HttpSession.newSession();
            /** 将session存入线程变量之中，方便后来的业务获取 */
            ThreadUtil.setHttpSession(httpSession);
            /** 解析参数列表 */
            requestParams = getRequestParams(request);

            /** 获取操作类型 */
            try {
                String path = requestParams.getString(KeyConstant.PATH);
                bizTypeEnum = getBizTypeByPath(path);
                if (bizTypeEnum == null)
                    throw new CourseWarn(SystemErrorEnum.BIZ_TYPE_NOT_EXIST);
                requestParams.put(KeyConstant.BIZ_TYPE, bizTypeEnum);
            } catch (Exception e) {
                throw new CourseWarn(SystemErrorEnum.BIZ_TYPE_NOT_EXIST);
            }

            /** 根据操作类型获取参数 */
            Class<CommonInParams> clz = dispatcher.getParamByBizType(bizTypeEnum);
            CommonInParams params = clz.newInstance();
            params.fromJsonObject(requestParams);

            /** 获取缓存在session中的用户名信息 */
            if (hasPreSession && !bizTypeEnum.equals(BizTypeEnum.USER_LOGIN)) {
                params.setUsername(httpSession.getUsername());
            }

            /** 使用派发器执行业务并返回业务执行结果 */
            String retStr;
            retStr = dispatcher.dispatch(params);
            writeResponse(channelHandlerContext, retStr, request);
        } catch (Exception e) {
            String retStr;
            if (e instanceof CourseWarn) {
                CourseWarn courseWarn = (CourseWarn)e;
                /** 记录警告日志 */
                LogUtil.WARN(null, bizTypeEnum, ParseUtil.getJSONString(requestParams), courseWarn);
                /** 返回警告的信息内容 */
                retStr = new SysWarnOutParams(courseWarn).toString();
            } else {
                /** 处理出错，记录日志 */
                LogUtil.ERROR(null, bizTypeEnum, ParseUtil.getJSONString(requestParams), e);
                /** 返回客户端INTERNAL_SERVER_ERROR，即服务器内部错误 */
                retStr = new SysErrorOutParams().toString();
            }

            /** 将返回结果写入管道 */
            writeResponse(channelHandlerContext, retStr, request);
        }
    }

    /** 解析http请求的参数 */
    private JSONObject getRequestParams(FullHttpRequest request) throws IOException {
        JSONObject params = new JSONObject();

        /** 解析请求URI中的参数 */
        String uri = request.uri();
        String[] uriParams = uri.split("\\?");
        /** 保存请求的路径 */
        params.put(KeyConstant.PATH, uriParams[0]);
        /** 如果uri中存在参数，保存参数列表 */
        if (uriParams.length > 1) {
            String[] paramList = uriParams[1].split("&");
            for (String param:paramList) {
                String[] keyVal = param.split("=");
                if (keyVal.length < 2)
                    continue;
                params.put(keyVal[0], keyVal[1]);
            }
        }
        /** 解析请求体中的参数 */
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE), request);
        if(request.content().isReadable()) {
            String jsonStr = request.content().toString(CharsetUtil.UTF_8);
            params.putAll(JSON.parseObject(jsonStr));
        }
        List<InterfaceHttpData> httpPostData = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : httpPostData) {
            /** 普通属性直接赋值就可以了 */
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MixedAttribute attribute = (MixedAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                /** 文件参数需要特殊处理 */
                FileUpload fileUpload = (FileUpload)data;
                params.put(data.getName(), fileUpload);
            }
        }
        return params;
    }

    /** 根据httpPath获取对应的业务 */
    private BizTypeEnum getBizTypeByPath(String httpPath) {
        BizTypeEnum[] bizTypeEnums = BizTypeEnum.values();
        BizTypeEnum ret = null;
        for (BizTypeEnum bizTypeEnum:bizTypeEnums) {
            if (httpPath.equals(bizTypeEnum.getHttpPath())) {
                ret = bizTypeEnum;
                break;
            }
        }
        return ret;
    }

    /** 根据请求的cookie获取HttpSession */
    private void getSession(FullHttpRequest msg) {
        hasPreSession = false;
        String cookieStr = msg.headers().get(HttpHeaderNames.COOKIE);
        if (cookieStr != null && !cookieStr.equals("")) {
            Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieStr);
            for (Cookie cookie:cookieSet) {
                if (cookie.name().equals(NameConstant.HTTP_SESSION_NAME))
                    if (HttpSession.sessionExist(cookie.value())) {
                        this.httpSession = HttpSession.getSession(cookie.value());
                        hasPreSession = true;
                        break;
                    }
            }
        }
    }

    /** 将内容写入返回管道中 */
    private void writeResponse(ChannelHandlerContext ctx, String content, FullHttpRequest request) {
        /** 将字符串写入response中 */
        ByteBuf buf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, NameConstant.DEFAULT_CONTENT);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "content-type");
        String clientIP = request.headers().get("Origin");
        if (clientIP != null)
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, clientIP);

        /** 如果之前不存在session，需要设置一下session */
        if (!hasPreSession) {
            Cookie cookie = new DefaultCookie(NameConstant.HTTP_SESSION_NAME, httpSession.getSessionId());
            cookie.setPath("/");
            String encodeCookie = ServerCookieEncoder.STRICT.encode(cookie);
            response.headers().set(HttpHeaderNames.SET_COOKIE,encodeCookie);
        }

        /** 写入管道中 */
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
