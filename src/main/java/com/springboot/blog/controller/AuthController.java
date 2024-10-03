package com.springboot.blog.controller;

import com.springboot.blog.dto.UserDto;
import com.springboot.blog.dto.auth.JwtAuthResponse;
import com.springboot.blog.dto.auth.LoginDto;
import com.springboot.blog.dto.auth.RegisterDto;
import com.springboot.blog.exception.ApiException;
import com.springboot.blog.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Authentication"
)
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // login user
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto){
        String response = authService.login(loginDto);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(response);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    // register user
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(HttpServletRequest request){
        String token = request.getHeader("Authorization");

        String[] tokenSplit = token.split(" ");

        if (tokenSplit.length < 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "invalid jwt token");
        }

        UserDto userDto = this.authService.getProfile(tokenSplit[1]);

        return ResponseEntity.ok(userDto);
    }
}
