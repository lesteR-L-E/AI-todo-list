package com.todo.aitodo.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 关闭 CSRF（前后端分离必须关）
                .csrf(csrf -> csrf.disable())


                .exceptionHandling(ex -> ex

                        //未登录返回401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未登录");
                        })

                        //已登录但没权限返回404
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限");
                        })
                )

                // 配置请求权限规则
                .authorizeHttpRequests(auth -> auth

                        // 放行 OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 放行登录/注册接口
                        .requestMatchers("/auth/**").permitAll()

                        // 其他所有请求必须登录
                        .anyRequest().authenticated()
                )
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);;

        return http.build();
    }
}