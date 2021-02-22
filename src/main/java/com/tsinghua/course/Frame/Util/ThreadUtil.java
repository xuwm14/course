package com.tsinghua.course.Frame.Util;

import com.tsinghua.course.Base.Model.User;
import io.netty.channel.ChannelHandlerContext;

/**
 * @描述 线程本地变量的操作组件
 **/
public class ThreadUtil {
    private static class ThreadParams {
        /** 当前用户 */
        private User nowUser = null;
        /** WebSocket管道，在WebSocket长连接中生效 */
        private ChannelHandlerContext channelHandlerContext = null;
        /** HTTPSession，在http请求中生效 */
        private HttpSession httpSession;

        public User getNowUser() {
            return nowUser;
        }

        public void setNowUser(User nowUser) {
            this.nowUser = nowUser;
        }

        public ChannelHandlerContext getChannelHandlerContext() {
            return channelHandlerContext;
        }

        public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
            this.channelHandlerContext = channelHandlerContext;
        }

        public HttpSession getHttpSession() {
            return httpSession;
        }

        public void setHttpSession(HttpSession httpSession) {
            this.httpSession = httpSession;
        }
    }

    private static ThreadLocal<ThreadParams> paramsThreadLocal = new ThreadLocal<>();

    private static ThreadParams getThreadParams() {
        ThreadParams threadParams = paramsThreadLocal.get();
        if (threadParams == null) {
            threadParams = new ThreadParams();
            paramsThreadLocal.set(threadParams);
        }
        return threadParams;
    }
    public static String getUsername() {
        User user = getUser();
        if (user == null)
            return null;
        return user.getUsername();
    }

    public static User getUser() {
        return getThreadParams().getNowUser();
    }

    public static void setUser(User user) {
        getThreadParams().setNowUser(user);
    }

    public static void setCtx(ChannelHandlerContext ctx) {
        getThreadParams().setChannelHandlerContext(ctx);
    }

    public static ChannelHandlerContext getCtx() {
        return getThreadParams().getChannelHandlerContext();
    }

    public static void setHttpSession(HttpSession httpSession) {
        getThreadParams().setHttpSession(httpSession);
    }

    public static HttpSession getHttpSession() {
        return getThreadParams().getHttpSession();
    }
    /** 清除本地线程变量，因为执行业务的线程存在复用情况，所以每次进入业务前需要调用此函数，防止两个业务使用一样的线程变量，导致内部逻辑出错 */
    public static void clean() {
        paramsThreadLocal.remove();
    }
}
