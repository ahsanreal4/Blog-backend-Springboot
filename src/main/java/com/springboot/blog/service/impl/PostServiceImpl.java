package com.springboot.blog.service.impl;

import com.springboot.blog.dto.comment.CommentDto;
import com.springboot.blog.dto.post.PostDto;
import com.springboot.blog.dto.post.PostResponseDto;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.model.Category;
import com.springboot.blog.model.Comment;
import com.springboot.blog.model.Post;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private ModelMapper mapper;
    private CategoryRepository categoryRepository;

    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        Post post = mapToEntity(postDto);
        post.setCategory(category);

        Post savedPost = postRepository.save(post);

        return mapToDto(savedPost);
    }

    @Override
    public PostResponseDto getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        // Calculate sort direction ( asc or desc )
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        // get content for page object
        List<Post> paginatedPosts = posts.getContent();

        List<PostDto> paginatedPostDtos = paginatedPosts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setContent(paginatedPostDtos);
        postResponseDto.setPageNo(posts.getNumber());
        postResponseDto.setPageSize(posts.getSize());
        postResponseDto.setTotalElements(posts.getTotalElements());
        postResponseDto.setTotalPages(posts.getTotalPages());
        postResponseDto.setLast(posts.isLast());

        return postResponseDto;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Set<Comment> comments = post.getComments();

        Set<CommentDto> commentDtos = comments.stream().map(comment -> mapper.map(comment, CommentDto.class)).collect(Collectors.toSet());

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setContent(post.getContent());
        dto.setCategoryId(post.getCategory().getId());
        dto.setComments(commentDtos);

        return dto;
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);

        Post updatedPost = postRepository.save(post);
        return mapToDto(updatedPost);
    }

    @Override
    public void deletePost(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        List<Post> posts = postRepository.findByCategoryId(categoryId);

        return posts.stream().map(post -> mapToDto(post))
                .collect(Collectors.toList());
    }

    // convert Entity to DTO
    private PostDto mapToDto(Post post){
        return mapper.map(post, PostDto.class);
    }

    // convert DTO to Entity
    private Post mapToEntity(PostDto postDto){
        return  mapper.map(postDto, Post.class);
    }
}
