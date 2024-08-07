package com.sparta.midgard.services.security;

import com.sparta.midgard.models.User;
import com.sparta.midgard.repositories.UserRepository;
import com.sparta.midgard.security.web.SecurityUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Web
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        return user
                .map(SecurityUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + username + " not found"));
    }
}
