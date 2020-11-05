package com.sht.deal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hongtao Shen
 * @date 2020/6/6 - 17:13
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {

        //添加head参数start
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("默认分组")
                //是否启动swagger
                .enable(true)
                .select()
                //配置扫描的包  RequestHandlerSelectors.any()  扫描全部
                .apis(RequestHandlerSelectors.basePackage("com.sht.deal.controller"))
                //过滤的路径  .paths(PathSelectors.ant("path"))
                .paths(PathSelectors.any())
                .build()
                // 设置请求头
                .globalOperationParameters(pars);
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     *
     * @return
     */
    private ApiInfo apiInfo() {

        Contact contact = new Contact("沈鸿涛", "http://eurasia.plus/swagger-ui.html", "shenhongtao12@aliyun.com");

        return new ApiInfoBuilder()
                .title("欧亚圈 APIs")
                //描述
                .description("测试api接口文档")
                //.termsOfServiceUrl("localhost:8877/swagger-ui.html")
                .termsOfServiceUrl("http://eurasia.plus/swagger-ui.html")
                .contact(contact)
                .version("1.0")
                .build();
    }
}
