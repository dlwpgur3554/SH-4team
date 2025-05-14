package com.port4dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/login.html", "/terms.html", "/signup.html", "/reset-password.html",
                               "/css/**", "/js/**", "/logo/**", "/header/**", "/portfolio/**").permitAll()
                .requestMatchers("/api/members/login", "/api/members/signup", "/api/members/reset-password").permitAll()
                .requestMatchers("/api/posts/**", "/api/portfolios/**").permitAll()
                .anyRequest().permitAll()  // 임시로 모든 요청 허용
            )
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                .logoutUrl("/api/members/logout")
                .logoutSuccessUrl("/login.html")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
