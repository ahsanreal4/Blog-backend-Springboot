package com.springboot.blog.dto.post;


import lombok.Data;

@Data
public class SinglePostDto extends PostDto {
    private String fileBase64;

}
