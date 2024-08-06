package com.sparta.midgard.controllers.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;
import com.sparta.midgard.security.api.JwtManager;
import com.sparta.midgard.services.SecurityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthenticationController {

    @Value("${jwt.cookie.name}")
    private String cookieName;

    private final AuthenticationManager authenticationManager;
    private final SecurityService securityService;
    private final JwtManager jwtManager;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, SecurityService securityService, JwtManager jwtManager) {
        this.authenticationManager = authenticationManager;
        this.securityService = securityService;
        this.jwtManager = jwtManager;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final UserDetails userDetails = securityService.loadUserByUsername(authRequest.getUsername());
            final String jwt = jwtManager.generateToken(userDetails);

            return constructCookie(response, jwt);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body("Incorrect username or password");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        return ResponseEntity.ok("Successfully logged out");
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewToken(HttpServletRequest request, HttpServletResponse response) {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String token = authorizationHeader.substring(7);
            final String username = jwtManager.extractUser(token);

            if (username != null) {
                try {
                    final UserDetails userDetails = securityService.loadUserByUsername(username);
                    if (jwtManager.validateToken(token, userDetails)) {
                        final String newToken = jwtManager.renewToken(token);

                        return constructCookie(response, newToken);
                    } else {
                        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                                .body("Invalid or expired token");
                    }
                } catch (UsernameNotFoundException e) {
                    return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                            .body("User not found");
                }
            } else {
                return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                        .body("Invalid authorization header");
            }
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body("Authorization header missing or incorrect");
        }
    }

    private ResponseEntity<?> constructCookie(HttpServletResponse response, String newToken) {
        Cookie cookie = new Cookie(cookieName, newToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthResponse(newToken));
    }

    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public record AuthResponse(String jwt) {
    }
}