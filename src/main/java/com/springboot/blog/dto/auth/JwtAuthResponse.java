package com.springboot.blog.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class JwtAuthResponse {
    private String accessToken;
    private final String tokenType = "Bearer";
}
