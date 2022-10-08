package com.zerobase.cms.zerobasecms;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@ServletComponentScan
@EnableJpaAuditing
@RequiredArgsConstructor
@EnableSwagger2
@SpringBootApplication
@EnableFeignClients
public class ZerobaseCmsApplication {


	public static void main(String[] args) {
		SpringApplication.run(ZerobaseCmsApplication.class, args);
	}

}
