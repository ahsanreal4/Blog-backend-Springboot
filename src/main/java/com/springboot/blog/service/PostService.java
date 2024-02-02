package com.springboot.blog.service;

import com.springboot.blog.dto.post.PostDto;
import com.springboot.blog.dto.post.PostResponseDto;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);
    PostResponseDto getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostDto getPostById(long id);
    PostDto updatePost(PostDto postDto, long id);
    void deletePost(long id);
    List<PostDto> getPostsByCategory(Long categoryId);
}
