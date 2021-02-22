package com.tsinghua.course;

import com.tsinghua.course.Frame.Config.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** SpringBoot的主启动类 */
@SpringBootApplication
public class CourseApplication implements CommandLineRunner {

    @Autowired
    NettyServer nettyServer;

    /** 启动服务器 */
    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }

    @Override
    public void run(String... args) {
        /** 监听Http，监听会阻塞线程，需要新建线程 */
        new Thread(() -> {
            try {
                nettyServer.startHttp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        /** 监听WebSocket，监听会阻塞线程，需要新建线程 */
        new Thread(() -> {
            try {
                nettyServer.startWebSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
