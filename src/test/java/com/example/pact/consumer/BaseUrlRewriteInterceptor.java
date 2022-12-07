package com.example.pact.consumer;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.core.NamedThreadLocal;

final class BaseUrlRewriteInterceptor implements RequestInterceptor {

    private static final ThreadLocal<String> baseUrlsHolder = new NamedThreadLocal<>("Base URLs");

    static void setBaseUrl(String baseUrl) {
        baseUrlsHolder.set(baseUrl);
    }

    static void resetBaseUrls() {
        baseUrlsHolder.remove();
    }

    @Override
    public void apply(RequestTemplate template) {
        template.target(baseUrlsHolder.get());
    }
}
