package com.example.my.apollo.portal.component;

import java.io.UnsupportedEncodingException;

import com.example.my.apollo.portal.component.config.PortalConfig;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 工厂Bean跟普通Bean不同，其返回的对象不是指定类的一个实例，
 * 其返回的是该工厂Bean的getObject方法所返回的对象 [也就是说getObject 想返回string,同时可以返回date]
 * 
 * 对类的创建之前进行初始化的操作，在afterPropertiesSet()中完成. [由InitializingBean 接口表示]
 * 
 * 一个复杂工厂类,由FactoryBean和InitializingBean两个接口表示
 */
@Component
public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {
    @Autowired
    private HttpMessageConverters httpMessageConverters;
    @Autowired
    private PortalConfig portalConfig;

    private RestTemplate restTemplate;

    public RestTemplate getObject() {
        return restTemplate;
    }

    public Class<RestTemplate> getObjectType() {
        return RestTemplate.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        restTemplate = new RestTemplate(httpMessageConverters.getConverters());
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(portalConfig.connectTimeout());
        requestFactory.setReadTimeout(portalConfig.readTimeout());

        restTemplate.setRequestFactory(requestFactory);
    }
}