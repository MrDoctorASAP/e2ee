package com.e2ee.api.spring.processors.docs;

import java.lang.reflect.Method;

public interface RestEndpointHandler {
    void handleEndpoint(Object bean, Method controllerMethod);
}
