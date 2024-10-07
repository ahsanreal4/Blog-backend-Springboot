package com.springboot.blog.dto.post;

import com.springboot.blog.dto.comment.CommentDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class PostDto {
    private Long id;

    // title should not be null or empty
    // title should have at least 5 characters
    @NotEmpty
    @Size(min = 5, message = "Post title should have at least 5 characters")
    private String title;

    @NotEmpty
    private String content;

    private Set<CommentDto> comments;

    @NotNull
    private Long categoryId;
}
