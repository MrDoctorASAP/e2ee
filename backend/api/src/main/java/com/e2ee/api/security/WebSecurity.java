package com.e2ee.api.security;

import com.e2ee.api.security.cors.CorsHeadersFilter;
import com.e2ee.api.security.jwt.JwtSecurityFilter;
import com.e2ee.api.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class WebSecurity {

    private final UserService service;
    private final JwtSecurityFilter jwtFilter;
    private final CorsHeadersFilter corsFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .formLogin().disable()
                .headers().frameOptions().sameOrigin().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/user/**").permitAll()
                    .requestMatchers("/api/v1/avatar/**").permitAll()
                    .requestMatchers("/ws/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated().and()
                .userDetailsService(service)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
