package com.ramirezabril.mobility_sharing.util;

import org.springframework.stereotype.Component;

@Component
public class TokenUtil {
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }
}
