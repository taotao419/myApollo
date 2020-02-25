package com.example.my.apollo.portal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import com.example.my.apollo.core.dto.ServiceDTO;
import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.portal.component.AdminServiceAddressLocator;
import com.example.my.apollo.portal.component.RetryableRestTemplate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class RetryableRestTemplateTest extends AbstractUnitTest {
    @Mock // // 创建一个Mock AdminServiceAddressLocator.
    private AdminServiceAddressLocator serviceAddressLocator;
    @Mock // 创建一个Mock restTemplate.
    private RestTemplate restTemplate;
    @InjectMocks // 创建一个实例，其余用@Mock注解创建的mock将被注入到用该实例中
    private RetryableRestTemplate retryableRestTemplate;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private String path = "app";
    private String serviceOne = "http://10.0.0.1";
    private String serviceTwo = "http://10.0.0.2";
    private String serviceThree = "http://10.0.0.3";
    private ResourceAccessException socketTimeoutException = new ResourceAccessException("");
    private ResourceAccessException httpHostConnectException = new ResourceAccessException("");
    private ResourceAccessException connectTimeoutException = new ResourceAccessException("");
    private Object request = new Object();
    private ResponseEntity<Object> entity = new ResponseEntity<>(HttpStatus.OK);

    @Test
    public void testPostSocketTimeoutNotRetry() {
        // 1. build mock method for serviceAddressLocator.getServiceList()
        when(serviceAddressLocator.getServiceList(any()))
                .thenReturn(Arrays.asList(mockService(serviceOne), mockService(serviceTwo), mockService(serviceThree)));

        when(restTemplate.postForEntity(serviceOne + "/" + path, request, Object.class)).thenThrow(socketTimeoutException);
        when(restTemplate.postForEntity(serviceTwo + "/" + path, request, Object.class)).thenReturn(entity);

        thrown.expect(Exception.class);
        retryableRestTemplate.post(Env.DEV, path,request,Object.class);

        verify(restTemplate).postForEntity(serviceOne + "/" + path, request, Object.class);
        verify(restTemplate,never()).postForEntity(serviceTwo + "/" + path, request, Object.class);
    }

    @Test
    public void testDelete() {
        // 1. build mock method for serviceAddressLocator.getServiceList()
        when(serviceAddressLocator.getServiceList(any()))
                .thenReturn(Arrays.asList(mockService(serviceOne), mockService(serviceTwo), mockService(serviceThree)));

        retryableRestTemplate.delete(Env.DEV, path);

        verify(restTemplate).delete(serviceOne + "/" + path);// 验证 restTemplate.delete("http://10.0.0.1/app") 被调用过一次
        verify(restTemplate, never()).delete(serviceTwo + "/" + path);// 验证 restTemplate.delete("http://10.0.0.2/app")
                                                                      // 没有调用过
    }

    private ServiceDTO mockService(String homeUrl) {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setHomepageUrl(homeUrl);
        return serviceDTO;
    }
}