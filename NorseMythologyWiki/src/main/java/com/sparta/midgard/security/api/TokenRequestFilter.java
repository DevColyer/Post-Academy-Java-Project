package com.sparta.midgard.security.api;


import com.sparta.midgard.services.SecurityService;
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

    private final SecurityService userDetailsService;
    private final JwtManager jwtManager;

    @Autowired
    public TokenRequestFilter(SecurityService userDetailsService, SecretKey key) {
        this.userDetailsService = userDetailsService;
        this.jwtManager = JwtManager.getInstance(key);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/authenticate")) {
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwt = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    jwt = cookie.getValue();
                    username = jwtManager.extractUser(jwt);
                    break;
                }
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwt != null && jwtManager.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (UsernameNotFoundException e) {
                logger.warn("Username not found for user: {}");
                logger.warn(e.getMessage());

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }

        }

        chain.doFilter(request, response);
    }
}