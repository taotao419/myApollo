package com.example.my.apollo.configservice.wrapper;

import java.util.List;
import java.util.Map;

import com.example.my.apollo.core.dto.ApolloConfigNotification;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * DeferredResultWrapper 延期结果? DeferredResult 和Callable的区别是
 * DeferredResult自己管执行线程 创建一个线程并将结果set到DeferredResult是由我们自己来做的。
 * 用completablefuture创建一个异步任务。这将创建一个新的线程，在那里我们的长时间运行的任务将被执行。
 * 也就是在这个线程中，我们将set结果到DeferredResult并返回。
 * 
 * 因为 DeferredResult 技术，所以使得 long polling 不会一直占用容器资源，使得长轮询成为可能。
 * 长轮询的应用有很多，简述下就是：需要及时知道某些消息的变更的场景都可以用长轮询来解决， 当然，你可能又想起了发布订阅了，哈哈
 */
public class DeferredResultWrapper {
    private static final ResponseEntity<List<ApolloConfigNotification>> NOT_MODIFIED_RESPONSE_LIST = new ResponseEntity<>(
            HttpStatus.NOT_MODIFIED);

    /**
     * 归一化( normalized )和原始( original )的 Namespace 的名字的 Map 。因为客户端在填写 Namespace
     * 时，写错了名字的大小写。在 Config Service 中，会进行归一化“修复”，方便逻辑的统一编写。 但是，最终返回给客户端需要“还原”回原始( *
     * original )的 Namespace 的名字，避免客户端无法识别。
     */
    private Map<String, String> normalizedNamespaceNameToOriginalNamespaceName;
    private DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> result;

    public DeferredResultWrapper(long timeoutInMilli) {
        result = new DeferredResult<>(timeoutInMilli, NOT_MODIFIED_RESPONSE_LIST);
    }

    public void recordNamespaceNameNormalizedResult(String originalNamespaceName, String normalizedNamespaceName) {
        if (normalizedNamespaceNameToOriginalNamespaceName == null) {
            normalizedNamespaceNameToOriginalNamespaceName = Maps.newHashMap();
        }
        normalizedNamespaceNameToOriginalNamespaceName.put(normalizedNamespaceName, originalNamespaceName);
    }

    public void onTimeout(Runnable timeoutCallback) {
        result.onTimeout(timeoutCallback);
    }

    public void onCompletion(Runnable completionCallback) {
        result.onCompletion(completionCallback);
    }

    public void setResult(ApolloConfigNotification notification) {
        setResult(Lists.newArrayList(notification));
    }

    /**
     * The namespace name is used as a key in client side, so we have to return the
     * original one instead of the correct one
     */
    public void setResult(List<ApolloConfigNotification> notifications) {
        if (normalizedNamespaceNameToOriginalNamespaceName != null) {
            notifications.stream()
                    .filter(notification -> normalizedNamespaceNameToOriginalNamespaceName
                            .containsKey(notification.getNamespaceName()))
                    .forEach(notification -> notification.setNamespaceName(
                            normalizedNamespaceNameToOriginalNamespaceName.get(notification.getNamespaceName())));
        }
        result.setResult(new ResponseEntity<>(notifications, HttpStatus.OK));
    }

    public DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> getResult() {
        return result;
    }
}