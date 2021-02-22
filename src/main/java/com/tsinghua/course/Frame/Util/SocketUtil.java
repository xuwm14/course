package com.tsinghua.course.Frame.Util;

import com.alibaba.fastjson.JSONArray;
import com.tsinghua.course.Biz.Controller.Params.CommonOutParams;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @描述 维护WebSocket长连接和对应用户的组件，socket是长连接，多次请求的管道不会变，可以直接用管道当key
 **/
public class SocketUtil {
    /** 用户名到管道的map */
    private static ConcurrentHashMap<String, ChannelHandlerContext> socketMap = new ConcurrentHashMap<>();
    /** 管道到用户名的map */
    private static ConcurrentHashMap<ChannelHandlerContext, String> ctxToUser = new ConcurrentHashMap<>();

    /** 设置用户管道 */
    public static void setUserSocket(String username, ChannelHandlerContext ctx) {
        ChannelHandlerContext preCtx = socketMap.get(username);
        if (preCtx != null)
            ctxToUser.remove(preCtx);
        socketMap.put(username, ctx);
        ctxToUser.put(ctx, username);
    }
    /** 根据管道获取用户名 */
    public static String getSocketUser(ChannelHandlerContext ctx) {
        return ctxToUser.get(ctx);
    }
    /** 移除管道 */
    public static void removeSocket(ChannelHandlerContext ctx) {
        String username = ctxToUser.get(ctx);
        ctxToUser.remove(ctx);
        if (username != null) {
            socketMap.remove(username);
        }
    }
    /** 向单个用户发送单个信息 */
    public static void sendMessageToUser(String username, CommonOutParams msgs) throws Exception {
        sendMessageToUser(username, Arrays.asList(msgs));
    }
    /** 向多个用户发送单个信息 */
    public static void sendMessageToUsers(Collection<String> usernames, CommonOutParams msgs) throws Exception {
        sendMessageToUsers(usernames, Arrays.asList(msgs));
    }
    /** 向多个用户发送多个信息 */
    public static void sendMessageToUsers(Collection<String> usernames, Collection<CommonOutParams> msgs) throws Exception {
        for (String username : usernames)
            sendMessageToUser(username, msgs);
    }
    /** 向单个用户发送多个信息 */
    public static void sendMessageToUser(String username, Collection<CommonOutParams> msgs) throws Exception {
        ChannelHandlerContext ctx = socketMap.get(username);
        JSONArray jsonArray = new JSONArray();
        for (CommonOutParams commonParams : msgs)
            jsonArray.add(commonParams.toJsonObject());
        if (ctx != null && ctx.channel().isActive()) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(jsonArray.toString()));
        }
    }
}
