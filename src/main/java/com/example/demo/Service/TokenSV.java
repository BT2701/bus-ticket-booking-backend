package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;
import com.example.demo.Repository.TokenRepo;
import com.example.demo.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenSV implements ITokenService{
    private static final int MAX_TOKENS = 3;

    private final TokenRepo tokenRepo;
    private final JwtUtils jwtUtils;

    @Value("${application.security.jwt.token-validity}")
    private Long accessTokenExpire;

    @Value("${application.security.jwt.refresh-token-validity}")
    private Long refreshTokenExpire;

    @Transactional
    @Override
    public Token addToken(Customer customer, String token) {
        List<Token> userTokens = tokenRepo.findByCustomer(customer);
        int tokenCount = userTokens.size();

        if(tokenCount >= MAX_TOKENS) {
            Token tokenToDelete = userTokens.get(0);
            tokenRepo.delete(tokenToDelete);
        }
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(accessTokenExpire);

        Token newToken = Token.builder()
                .customer(customer)
                .accessToken(token)
                .isAccessExpired(false)
                .accessExpirationDate(expirationDateTime)
                .build();

        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(refreshTokenExpire));
        tokenRepo.save(newToken);

        return newToken;
    }

    @Override
    public Token refreshToken(String refreshToken, Customer customer) throws Exception {
        Token jwtToken = tokenRepo.findByRefreshToken(refreshToken);

        String token = jwtUtils.generateToken(customer);

        jwtToken.setAccessToken(token);
        jwtToken.setAccessExpirationDate(LocalDateTime.now().plusSeconds(accessTokenExpire));
        jwtToken.setAccessExpired(false);

        jwtToken.setRefreshToken(UUID.randomUUID().toString());
        jwtToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(refreshTokenExpire));
        tokenRepo.save(jwtToken);

        return jwtToken;
    }
}
