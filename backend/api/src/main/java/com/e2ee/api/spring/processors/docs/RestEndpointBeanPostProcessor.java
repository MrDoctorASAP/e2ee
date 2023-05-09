package com.e2ee.api.spring.processors.docs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RestEndpointBeanPostProcessor implements BeanPostProcessor {

    private final List<RestEndpointHandler> handlers;

    private static final List<Class<? extends Annotation>> annotations = List.of(
            GetMapping.class,
            PostMapping.class,
            PutMapping.class,
            PatchMapping.class,
            DeleteMapping.class,
            RequestMapping.class
    );

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RestController.class)) {
            for (Method method : bean.getClass().getMethods()) {
                if (annotations.stream().anyMatch(method::isAnnotationPresent)) {
                    handlers.forEach(handler -> handler.handleEndpoint(bean, method));
                }
            }
        }
        return bean;
    }

}
