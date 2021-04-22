package com.tsinghua.course;

import com.tsinghua.course.Frame.Config.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/** SpringBoot的主启动类 */
@SpringBootApplication
public class CourseApplication implements CommandLineRunner {

    @Autowired
    NettyServer nettyServer;

    /** 启动服务器 */
    public static void main(String[] args) {
        try {
            SpringApplication.run(CourseApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) {
        /** 设置时区，在某些服务器上时区可能不是标准北京时区 */
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
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
