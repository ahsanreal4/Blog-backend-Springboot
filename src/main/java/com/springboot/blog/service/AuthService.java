package com.springboot.blog.service;

import com.springboot.blog.dto.UserDto;
import com.springboot.blog.dto.auth.LoginDto;
import com.springboot.blog.dto.auth.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);

    UserDto getProfile(String token);
}
