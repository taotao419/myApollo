package com.example.my.apollo.portal.component;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.annotation.PostConstruct;

import com.example.my.apollo.common.exception.ServiceException;
import com.example.my.apollo.core.dto.ServiceDTO;
import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.portal.constant.TracerEventType;
import com.example.my.apollo.tracer.Tracer;
import com.example.my.apollo.tracer.spi.Transaction;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

@Component
public class RetryableRestTemplate {
    private Logger logger = LoggerFactory.getLogger(RetryableRestTemplate.class);
    private UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory();
    private final AdminServiceAddressLocator adminServiceAddressLocator;
    private RestTemplate restTemplate;

    private final RestTemplateFactory restTemplateFactory;

    public RetryableRestTemplate(final @Lazy RestTemplateFactory restTemplateFactory,
            final @Lazy AdminServiceAddressLocator adminServiceAddressLocator) {
        this.restTemplateFactory = restTemplateFactory;
        this.adminServiceAddressLocator = adminServiceAddressLocator;
    }
    
    @PostConstruct
    private void postConstruct() {
      restTemplate = restTemplateFactory.getObject();
    }
    
    public <T> T get(Env env, String path, Class<T> responseType, Object... urlVariables) throws RestClientException {
        return execute(HttpMethod.GET, env, path, null, responseType, urlVariables);
    }

    public <T> T post(Env env, String path, Object request, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        return execute(HttpMethod.POST, env, path, request, responseType, uriVariables);
    }

    public void put(Env env, String path, Object request, Object... urlVariables) throws RestClientException {
        execute(HttpMethod.PUT, env, path, request, null, urlVariables);
    }

    public void delete(Env env, String path, Object... urlVariables) throws RestClientException {
        execute(HttpMethod.DELETE, env, path, null, null, urlVariables);
    }

    private <T> T execute(HttpMethod method, Env env, String path, Object request, Class<T> responseType,
            Object... uriVariables) {

        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }

        String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
        Transaction ct = Tracer.newTransaction("AdminAPI", uri);
        ct.addData("Env", env);

        List<ServiceDTO> services = getAdminServices(env, ct);

        for (ServiceDTO serviceDTO : services) {
            try {

                T result = doExecute(method, serviceDTO, path, request, responseType, uriVariables);

                ct.setStatus(Transaction.SUCCESS);
                ct.complete();
                return result;
            } catch (Throwable t) {
                logger.error("Http request failed, uri: {}, method: {}", uri, method, t);
                Tracer.logError(t);
                if (canRetry(t, method)) {
                    Tracer.logEvent(TracerEventType.API_RETRY, uri);
                } else {// biz exception rethrow
                    ct.setStatus(t);
                    ct.complete();
                    throw t;
                }
            }
        }

        // all admin server down
        ServiceException e = new ServiceException(
                String.format("Admin servers are unresponsive. meta server address: N/A, admin servers: N/A"));
        ct.setStatus(e);
        ct.complete();
        throw e;
    }

    private List<ServiceDTO> getAdminServices(Env env, Transaction ct) {

        List<ServiceDTO> services = adminServiceAddressLocator.getServiceList(env);

        if (CollectionUtils.isEmpty(services)) {
            ServiceException e = new ServiceException(String.format("No available admin server."
                    + " Maybe because of meta server down or all admin server down. " + "Meta server address: N/A"));
            ct.setStatus(e);
            ct.complete();
            throw e;
        }

        return services;
    }

    private <T> T doExecute(HttpMethod method, ServiceDTO service, String path, Object request, Class<T> responseType,
            Object... uriVariables) {
        T result = null;
        switch (method) {
            case GET:
                result = restTemplate.getForObject(parseHost(service) + path, responseType, uriVariables);
                break;
            case POST:
                result = restTemplate.postForEntity(parseHost(service) + path, request, responseType, uriVariables)
                        .getBody();
                break;
            case PUT:
                restTemplate.put(parseHost(service) + path, request, uriVariables);
                break;
            case DELETE:
                restTemplate.delete(parseHost(service) + path, uriVariables);
                break;
            default:
                throw new UnsupportedOperationException(String.format("unsupported http method(method=%s)", method));
        }
        return result;
    }

    private String parseHost(ServiceDTO serviceAddress) {
        return serviceAddress.getHomepageUrl() + "/";
    }

    // post,delete,put请求在admin server处理超时情况下不重试
    private boolean canRetry(Throwable e, HttpMethod method) {
        Throwable nestedException = e.getCause();
        if (method == HttpMethod.GET) {
            return nestedException instanceof SocketTimeoutException
                    || nestedException instanceof HttpHostConnectException
                    || nestedException instanceof ConnectTimeoutException;
        } else {
            return nestedException instanceof HttpHostConnectException
                    || nestedException instanceof ConnectTimeoutException;
        }
    }
}