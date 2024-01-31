package com.springboot.blog.service;

import com.springboot.blog.dto.comment.CommentDto;

public interface CommentService {
    CommentDto createComment(long postId, CommentDto commentDto);
}
