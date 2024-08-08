package com.sparta.midgard;

import com.sparta.midgard.models.User;
import com.sparta.midgard.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class NorseMythologyWikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NorseMythologyWikiApplication.class, args);
    }

//    @Bean
//    CommandLineRunner setup(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            Set<String> adminRoles = new HashSet<>();
//            adminRoles.add("ROLE_ADMIN");
//            userRepository.save(new User("admin", passwordEncoder.encode("password"), adminRoles));
//
//            Set<String> userRoles = new HashSet<>();
//            userRoles.add("ROLE_USER");
//            userRepository.save(new User("user", passwordEncoder.encode("password"), userRoles));
//        };
//    }

}
