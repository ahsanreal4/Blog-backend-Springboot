package com.springboot.blog.service;

import com.springboot.blog.dto.post.PostDto;
import com.springboot.blog.dto.post.PostResponseDto;
import com.springboot.blog.dto.post.SinglePostDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto, MultipartFile file);
    PostResponseDto getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    SinglePostDto getPostById(long id);
    PostDto updatePost(PostDto postDto, long id);
    void deletePost(long id);
    List<PostDto> getPostsByCategory(Long categoryId);
}
