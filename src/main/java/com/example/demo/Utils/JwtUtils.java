package com.example.demo.Utils;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;
import com.example.demo.Repository.TokenRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.io.Decoders;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {
    @Autowired
    private TokenRepo tokenRepo;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.reset-token-validity}")
    private Long resetTokenExpire;
    @Value("${application.security.jwt.token-validity}")
    private Long accessTokenExpire;

    public String generateToken(Customer customer) throws Exception {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("phoneNumber", customer.getPhone());

        try {
            // mã hóa từng cái trong claims
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(customer.getPhone())
                    .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpire * 1000L)) // chuyển từ s sang date
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();

            return token;
        } catch (Exception e) {
            throw  new Exception("Cannot create jwts token: " + e.getMessage());
        }
    }

    public String generateResetToken(Customer customer) throws Exception {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("phoneNumber", customer.getPhone());

        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(customer.getPhone())
                    .setExpiration(new Date(System.currentTimeMillis() + resetTokenExpire * 1000L)) // chuyển từ s sang date
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();

            return token;
        } catch (Exception e) {
            throw  new Exception("Cannot create jwts token: " + e.getMessage());
        }
    }

    public Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        Token exsistingToken = tokenRepo.findByAccessToken(token);

        if(exsistingToken == null) {
            return false;
        }

        return (phoneNumber.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
