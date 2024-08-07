package com.sparta.midgard.security.api;

import com.sparta.midgard.services.security.SecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

@Component
public class TokenRequestFilter extends OncePerRequestFilter {

    @Value("${jwt.cookie.name}")
    private String cookieName;

    private final SecurityService securityService;
    private final JwtManager jwtManager;

    @Autowired
    public TokenRequestFilter(SecurityService securityService, SecretKey key) {
        this.securityService = securityService;
        this.jwtManager = JwtManager.getInstance(key);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (!requestURI.startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = extractJwtFromCookies(request.getCookies());

        if (jwt != null) {
            String username = jwtManager.extractUser(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = securityService.loadUserByUsername(username);

                    if (jwtManager.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        logger.warn("Invalid JWT token for user: " + username);
                    }
                } catch (UsernameNotFoundException e) {
                    logger.warn("Username not found for user: " + username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Username not found");
                    return;
                } catch (Exception e) {
                    logger.error("Error processing JWT token", e);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private String extractJwtFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
