package com.e2ee.api.spring.processors.docs;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class RestEndpointDtoCollector implements RestEndpointHandler {

    private final Set<Class<?>> dtoObjects = new HashSet<>();

    @Override
    public void handleEndpoint(Object bean, Method controllerMethod) {
        System.out.println("Endpoint: " + bean.getClass().getSimpleName() + " -> " + controllerMethod);
        Class<?> returnType = controllerMethod.getReturnType();
        if (returnType != void.class && returnType != Void.class) {
            System.out.println("Response body : " + returnType.getSimpleName());
            dtoObjects.add(returnType);
        }
        Class<?>[] parameterTypes = controllerMethod.getParameterTypes();
        Annotation[][] parameterAnnotations = controllerMethod.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (Arrays.stream(parameterAnnotations[i])
                    .map(Object::getClass).anyMatch(RequestBody.class::equals)) {
                System.out.println("Request body : " + parameterTypes[i].getSimpleName());
                dtoObjects.add(parameterTypes[i]);
            }
        }
    }

    public Set<Class<?>> getDtoObjects() {
        return Collections.unmodifiableSet(dtoObjects);
    }

}
