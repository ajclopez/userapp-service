package com.company.userapp.util;

import com.company.userapp.exception.ForbiddenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JWTUtil {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private JWTUtil() {

    }

    public static String generateJwtToken(String sub, String secret) {

        return Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static void isValidToken(String bearer, String token) {

        if ( !bearer.replace(TOKEN_PREFIX, "").trim().equals(token) ) {
            throw new ForbiddenException("Forbidden request");
        }
    }
}
