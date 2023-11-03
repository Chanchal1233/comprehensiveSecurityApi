package com.security.gas.plant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
public class GasPlantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GasPlantServiceApplication.class, args);
    }


}
