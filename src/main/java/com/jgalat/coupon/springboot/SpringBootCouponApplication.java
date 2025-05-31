package com.jgalat.coupon.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import com.jgalat.coupon.springboot.configuration.SwaggerConfiguration;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaRepositories
@EnableJpaAuditing
@EnableRetry
@EnableConfigurationProperties(SwaggerConfiguration.class)  
public class SpringBootCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCouponApplication.class, args);
    }
}
