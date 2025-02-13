package com.kaushika.coreapi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EndpointScanner {
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void scanEndpoints() {
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        List<String> endpoints = new ArrayList<>();
        System.out.println("\n=== Available API Endpoints ===\n");

        handlerMethods.forEach((key, value) -> {
            if (key != null && key.getPatternValues() != null) {
                key.getPatternValues().forEach(pattern -> {
                    key.getMethodsCondition().getMethods().forEach(method -> {
                        String endpoint = String.format("%s %s -> %s.%s",
                                method,
                                pattern,
                                value.getBeanType().getSimpleName(),
                                value.getMethod().getName());
                        System.out.println(endpoint);
                    });
                });
            }
        });

        System.out.println("\n===========================\n");
    }
}