package com.example.demo.filter;

import com.example.demo.Model.Customer;
import com.example.demo.Utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtTokenUtils;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException
    {
        try {
            if(isBypassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn không có quyền truy cập tài nguyên này!");
                return;
            }

            final String token = authHeader.substring(7); // cắt bỏ "Bearer "
            final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);

            if(phoneNumber != null ) { // && SecurityContextHolder.getContext().getAuthentication() == null
                Customer userDetails = (Customer) userDetailsService.loadUserByUsername(phoneNumber);

                if (!userDetails.isActive()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("Tài khoản đã bị khóa!");
                    response.getWriter().flush();
                    return;
                }

                if(jwtTokenUtils.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn không có quyền truy cập tài nguyên này!");
        }
    }

    private boolean isBypassToken(@NotNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                // cho tất cả request
//                Pair.of("/", "GET"),
//                Pair.of("/", "POST"),
//                Pair.of("/", "PUT"),
//                Pair.of("/", "DELETE")

                Pair.of(String.format("%s/buslist", apiPrefix), "GET"),

                Pair.of(String.format("%s/customers/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/customers/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/customers/oauth2-logout", apiPrefix), "POST"),
                Pair.of(String.format("%s/customers/forgotPassword", apiPrefix), "POST"),
                Pair.of(String.format("%s/mail/sendEmailToForgotPassword", apiPrefix), "POST"),
                Pair.of(String.format("%s/customers/oauth2-create-password", apiPrefix), "POST"),

                Pair.of(String.format("%s/customers/verify", apiPrefix), "GET"),
                Pair.of(String.format("%s/customers/oauth2-infor", apiPrefix), "GET"),
                Pair.of(String.format("%s/customers/oauth2-update-infor-on-first-login", apiPrefix), "PUT"),
                Pair.of("http://localhost:8080/oauth2/authorization/google", "GET"),
                Pair.of("http://localhost:8080/oauth2/authorization/facebook", "GET"),
                Pair.of("http://localhost:8080/oauth2/authorization/github", "GET"),

                Pair.of(String.format("%s/drivers/avatar", apiPrefix), "GET"),
                Pair.of(String.format("%s/buses/img", apiPrefix), "GET")
        );

        for(Pair<String, String> bypassToken : bypassTokens) {
            if(request.getServletPath().contains(bypassToken.getFirst()) && request.getMethod().equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}