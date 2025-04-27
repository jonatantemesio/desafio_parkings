package com.estapar.parking_management.exceptions;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);
        Throwable error = getError(webRequest);
        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("timestamp", LocalDateTime.now());
        customAttributes.put("status", defaultErrorAttributes.get("status"));
        customAttributes.put("message", error != null ? error.getMessage() : "Unexpected error"); // <- Aqui corrigido
        customAttributes.put("path", defaultErrorAttributes.get("path"));

        return customAttributes;
    }
}
