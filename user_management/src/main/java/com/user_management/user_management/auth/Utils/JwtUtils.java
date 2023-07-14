package com.user_management.user_management.auth.Utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class JwtUtils {
    static Key jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("aerkjfsdanfjsd9865agcdvhtdf265494651kdnfhfaljkasfdj"));
    
    public static String generateToken(String userId, int expiredIn){
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "HS256")
            .claim("userId", userId)
            .setExpiration(new Date(System.currentTimeMillis() + expiredIn))
            .signWith(jwtKey)
            .compact();
    }

    public static Claims getTokenClaims(String token){
        return Jwts.parserBuilder()
            .setSigningKey(jwtKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
