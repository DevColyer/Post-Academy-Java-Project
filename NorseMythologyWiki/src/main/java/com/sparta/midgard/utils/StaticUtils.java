package com.sparta.midgard.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.regex.Pattern;

public class StaticUtils {
    public static String getPartialMatcherRegex(String searchTerm){
        return "(?i).*" + Pattern.quote(searchTerm) + ".*";
    }

    public static String getRequestBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme).append("://").append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            baseUrl.append(":").append(serverPort);
        }

        return baseUrl.toString();
    }

    public static boolean isRoleAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        return userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
