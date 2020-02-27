package com.example.my.apollo.metaservice;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.web.firewall.DefaultHttpFirewall;
// import org.springframework.security.web.firewall.HttpFirewall;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = ApolloMetaServiceConfig.class)
public class ApolloMetaServiceConfig {
    // @Bean
    // public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
    //     /**
    //      * DefaultHttpFirewall是Spring Security Web提供的一个HTTP防火墙(对应概念模型接口HttpFirewall)实现.
    //      * 该实现是所谓的缺省实现，但实际上Spring Security Web缺省使用的并不是DefaultHttpFirewall,
    //      * 而是严格模式的StrictHttpFirewall。其原因主要是StrictHttpFirewall对安全限制更严格 这里标准化的URL必须符合以下条件
    //      * : 1. 指定路径中，必须不能包含以下字符串序列之一 : ["//","./","/…/","/."]
    //      * 
    //      * 2. 如果请求URL（URL编码后)包含了斜杠(%2f或者%2F)则该请求会被拒绝。
    //      * 
    //      * 如果请求违反了以上安全规则中的任何一条，DefaultHttpFirewall会通过抛出异常RequestRejectedException拒绝该请求。
    //      */
    //     return new DefaultHttpFirewall();
    // }

}