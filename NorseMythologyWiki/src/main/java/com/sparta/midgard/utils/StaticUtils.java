package com.sparta.midgard.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
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

    public static void addHateoasLink(RepresentationModel<?> model, String baseUrl, String path, String rel) {
        model.add(Link.of(baseUrl + path).withRel(rel));
    }

    public static String extractJwtFromCookies(Cookie[] cookies, String jwtTokenName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtTokenName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
