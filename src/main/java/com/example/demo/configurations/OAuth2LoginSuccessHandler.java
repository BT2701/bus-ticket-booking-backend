package com.example.demo.configurations;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Role;
import com.example.demo.Repository.CustomerRepo;
import com.example.demo.Repository.ProviderType;
import com.example.demo.Repository.RoleRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final CustomerRepo customerRepo;
    private final RoleRepo roleRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String provider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        String id = Objects.equals(provider, "google") ? "email" : "id";

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        String email = attributes.get("email") == null ? attributes.get("login").toString() + "@gmail.com" : attributes.get("email").toString();
        String name = attributes.getOrDefault("name", "").toString();
        customerRepo.findCustomerByEmail(email)
                .ifPresentOrElse(user -> {
                    user.setProvider(ProviderType.valueOf(provider.toUpperCase()));
                    user.setVerified(true);
                    customerRepo.save(user);

                    DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(user.getRole().getName())),
                            attributes, id);
                    Authentication securityAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority(user.getRole().getName())),
                            oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                    SecurityContextHolder.getContext().setAuthentication(securityAuth);
                }, () -> {
                    Role role = roleRepo.findByName(Role.ROLE_CUSTOMER);

                    Customer userEntity = new Customer();
                    userEntity.setRole(role);
                    userEntity.setEmail(email);
                    userEntity.setName(name);
                    userEntity.setProvider(ProviderType.valueOf(provider.toUpperCase()));
                    userEntity.setActive(true);
                    userEntity.setVerified(true);
                    customerRepo.save(userEntity);

                    DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(userEntity.getRole().getName())),
                            attributes, id);
                    Authentication securityAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority(userEntity.getRole().getName())),
                            oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                    SecurityContextHolder.getContext().setAuthentication(securityAuth);
                });

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:3000/login");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
