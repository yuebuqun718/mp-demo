package com.itheima.mp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.itheima.mp.mapper")
@SpringBootApplication
public class MpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MpDemoApplication.class, args);
        System.out.println("hello mp...1");
        System.out.println("hello mp...2");
        System.out.println("hello mp...3");
        System.out.println("hello mp...55555555");
        System.out.println("hello mp...66666666");
        System.out.println("hello mp...88888888");
        System.out.println("hello mp...99999999");
        System.out.println("hot-fix test...");
    }

}

