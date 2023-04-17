package com.messanger.auth.security;

import com.messanger.auth.security.jwt.JwtSecurityFilter;
import com.messanger.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService service;
    private final JwtSecurityFilter jwtFilter;

    @Autowired
    public WebSecurity(UserService service, JwtSecurityFilter jwtFilter) {
        this.service = service;
        this.jwtFilter = jwtFilter;
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests().antMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated()
                .and()
            .userDetailsService(service)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
