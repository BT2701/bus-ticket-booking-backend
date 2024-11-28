package com.example.demo.configurations;

import com.example.demo.Model.Role;
import com.example.demo.filter.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
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
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
//                            .anyRequest().permitAll();

                            .requestMatchers(
                                    String.format("%s/customers/login", apiPrefix),
                                    String.format("%s/customers/register", apiPrefix),
                                    String.format("%s/customers/forgotPassword", apiPrefix),
                                    String.format("%s/customers/oauth2-update-infor-on-first-login", apiPrefix),
                                    String.format("%s/customers/verify", apiPrefix),
                                    String.format("%s/customers/oauth2-infor", apiPrefix),
                                    String.format("%s/customers/oauth2-logout", apiPrefix),
                                    String.format("%s/customers/oauth2-create-password", apiPrefix),

                                    String.format("%s/mail/sendEmailToForgotPassword", apiPrefix),

                                    "http://localhost:8080/oauth2/authorization/google",
                                    "http://localhost:8080/oauth2/authorization/facebook",
                                    "http://localhost:8080/oauth2/authorization/github",

                                    String.format("%s/buslist", apiPrefix),
                                    String.format("%s/drivers/avatar/**", apiPrefix),
                                    String.format("%s/buses/img/**", apiPrefix),

                                    // bổ sung thêm các API còn lại
                                    String.format("%s/booking**", apiPrefix),
                                    String.format("%s/schedule**", apiPrefix),
                                    String.format("%s/feedback/**", apiPrefix),
                                    String.format("%s/feedback/average/**", apiPrefix),
                                    String.format("%s/feedback/count/**", apiPrefix),
                                    String.format("%s/get-all-routes**", apiPrefix),
                                    String.format("%s/get-from-to", apiPrefix),
                                    String.format("%s/search'", apiPrefix),
                                    String.format("%s/lookup-invoice", apiPrefix),
                                    String.format("%s/cancel-ticket/**", apiPrefix),
                                    String.format("%s/contact", apiPrefix),
                                    String.format("%s/routes/popular/**", apiPrefix)
                            ).permitAll()

                            // customer
                            .requestMatchers("POST", String.format("%s/customers/refreshToken", apiPrefix)).authenticated()
                            .requestMatchers("PUT", String.format("%s/customers/updatePassword", apiPrefix)).authenticated()
//                            .requestMatchers("POST", String.format("%s/customers/oauth2-logout", apiPrefix)).authenticated()
                            .requestMatchers("POST", String.format("%s/customers/logout", apiPrefix)).authenticated()
                            .requestMatchers("POST", String.format("%s/customers/logoutAll", apiPrefix)).authenticated()

                            .requestMatchers("PUT", String.format("%s/customers/lock/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/customers/unlock/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/customers/updateUserFromAdmin/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)

                            .requestMatchers("GET", String.format("%s/customers**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/customers/details", apiPrefix)).hasAnyRole(Role.ROLE_CUSTOMER, Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/customers/**", apiPrefix)).hasAnyRole(Role.ROLE_CUSTOMER, Role.ROLE_STAFF, Role.ROLE_ADMIN)

                            // roles
                            .requestMatchers("GET", String.format("%s/roles**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/roles/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/roles", apiPrefix)).hasRole(Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/roles/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
                            .requestMatchers("DELETE", String.format("%s/roles/**", apiPrefix)).hasRole(Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/roles/assign", apiPrefix)).hasRole(Role.ROLE_ADMIN)

                            // schedules
                            .requestMatchers("GET", String.format("%s/schedule/driver/**", apiPrefix)).authenticated()

                            // buses
                            .requestMatchers("POST", String.format("%s/assignDriverToBus", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/buses", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/buses/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("DELETE", String.format("%s/buses/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)

                            // drivers
                            .requestMatchers("GET", String.format("%s/drivers**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/drivers**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/drivers/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/drivers/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/drivers**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)


                            // bổ sung thêm các API còn lại
                            .requestMatchers("GET", String.format("%s/lookup-past-bookings**", apiPrefix)).authenticated()
                            .requestMatchers("POST", String.format("%s/addfeedback", apiPrefix)).authenticated()
                            .requestMatchers("GET", String.format("%s/notification/**", apiPrefix)).authenticated()
                            .requestMatchers("PUT", String.format("%s/notification/**", apiPrefix)).authenticated()

                            .requestMatchers("GET", String.format("%s/booked-seats**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/user**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/schedules**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/booking**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/booking/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/booking/total", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/booking-management**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("DELETE", String.format("%s/booking/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/payment**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/busesLimit**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/route-management**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/schedule**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/route/total", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/schedule/total", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/schedule-management**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)

                            .requestMatchers("GET", String.format("%s/statistic/tinhtrangve", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/statistic/tuyenxephobien/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/statistic/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)

                            .requestMatchers("GET", String.format("%s/contacts**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("POST", String.format("%s/send-mail-and-update-status", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("GET", String.format("%s/staff-lookup-invoice/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)
                            .requestMatchers("PUT", String.format("%s/update-ticket-status/**", apiPrefix)).hasAnyRole(Role.ROLE_STAFF, Role.ROLE_ADMIN)

                            .anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:3000/login", true)
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        e->e
//                                .accessDeniedHandler((request, response, accessDeniedException)->response.setStatus(403))
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write("Bạn không có quyền truy cập tài nguyên này!");
                                    response.getWriter().flush();
                                })
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:3000"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });

        return http.build();
    }
}