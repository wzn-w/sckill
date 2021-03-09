package com.killserver1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class KillServer1Application {

    public static void main(String[] args) {
        SpringApplication.run(KillServer1Application.class, args);
    }
}
