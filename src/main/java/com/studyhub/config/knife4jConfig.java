package com.studyhub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class knife4jConfig {

    @Bean
    public OpenAPI opneapi(){
        return new OpenAPI()
                .info(new Info()
                        .title("秋招备战系统 API")
                        .description("八股知识库模块接口文档")
                        .version("v0.4"));
    }
}
