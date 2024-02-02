package com.springboot.blog.security;

import com.springboot.blog.exception.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // generate JWT token
    public String generateToken(Authentication authentication){
        String username = authentication.getName();

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key())
                .compact();

        return token;
    }

    // get username from jwt token
    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // validate jwt token
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        }
        catch (MalformedJwtException malformedJwtException){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Jwt token");
        }
        catch (ExpiredJwtException expiredJwtException){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Expired Jwt token");
        }
        catch (UnsupportedJwtException unsupportedJwtException){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported Jwt token");
        }
        catch (IllegalArgumentException illegalArgumentException){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Jwt claims string is null or empty");
        }
    }

    // Decode base64 key
    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

}
