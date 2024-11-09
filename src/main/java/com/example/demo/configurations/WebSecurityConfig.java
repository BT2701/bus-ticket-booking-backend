package com.example.demo.configurations;

import com.example.demo.Model.Role;
import com.example.demo.Utils.CustomAuthorizationManager;
import com.example.demo.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final CustomAuthorizationManager customAuthorizationManager;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .anyRequest().permitAll();
//                            .requestMatchers("/api/customers/unlock**", "/api/customers/unlock**").hasRole(Role.ROLE_ADMIN)
//                            .requestMatchers("/api/customers/login", "/api/customers/login/**","/api/customers/register", "/api/customers/register/**").permitAll()
//                            .anyRequest().access(customAuthorizationManager);

//                            .requestMatchers(
//                                    String.format("%s/customers/login", apiPrefix),
//                                    String.format("%s/customers/register", apiPrefix),
//                                    String.format("%s/customers/refreshToken", apiPrefix),
//                                    String.format("%s/customers/forgotPassword", apiPrefix),
//                                    String.format("%s/sendEmailToForgotPassword", apiPrefix),
//                                    String.format("%s/buslist", apiPrefix)
//                            ).permitAll()
//
//                            .requestMatchers("GET", String.format("%s/customers/details", apiPrefix)).hasAnyRole(Role.ROLE_USER, Role.ROLE_ADMIN)
//                            .requestMatchers("POST", String.format("%s/customers/logout", apiPrefix)).hasAnyRole(Role.ROLE_USER, Role.ROLE_ADMIN)
//                            .requestMatchers("POST", String.format("%s/customers/logoutAll", apiPrefix)).hasAnyRole(Role.ROLE_USER, Role.ROLE_ADMIN)
//                            .requestMatchers("PUT", String.format("%s/customers/**", apiPrefix)).hasAnyRole(Role.ROLE_USER, Role.ROLE_ADMIN)
//
//                            // roles
//                            .requestMatchers("POST", String.format("%s/roles", apiPrefix)).hasRole(Role.ROLE_ADMIN)
//                            .requestMatchers("GET", String.format("%s/roles**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
//                            .requestMatchers("GET", String.format("%s/roles/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
//                            .requestMatchers("PUT", String.format("%s/roles/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
//                            .requestMatchers("DELETE", String.format("%s/roles/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
//                            .requestMatchers("POST", String.format("%s/roles/assign", apiPrefix)).hasRole(Role.ROLE_ADMIN)
//
//                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);
//                .exceptionHandling(
//                        e->e
//                                .accessDeniedHandler((request, response, accessDeniedException)->response.setStatus(403))
//                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                );
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });

        return http.build();
    }
}