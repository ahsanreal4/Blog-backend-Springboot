package com.springboot.blog.service.impl;

import com.springboot.blog.dto.post.PostDto;
import com.springboot.blog.dto.post.PostResponseDto;
import com.springboot.blog.dto.post.SinglePostDto;
import com.springboot.blog.exception.ApiException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.model.Category;
import com.springboot.blog.model.Post;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.FileService;
import com.springboot.blog.service.PostService;
import com.springboot.blog.service.StorageService;
import com.springboot.blog.utils.constants.FileTypes;
import org.springframework.core.io.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private ModelMapper mapper;
    private CategoryRepository categoryRepository;
    private StorageService storageService;
    private FileService fileService;

    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper, CategoryRepository categoryRepository, StorageService storageService, FileService fileService) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
        this.fileService = fileService;
    }

    @Override
    public PostDto createPost(PostDto postDto, MultipartFile file) {
        String fileExtension = fileService.getFileExtension(file.getOriginalFilename());

        if (fileExtension == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File extension not found");
        }

        boolean isValidFileExtension = fileExtension.equalsIgnoreCase(FileTypes.SVG.toString()) ||
                fileExtension.equalsIgnoreCase(FileTypes.PNG.toString()) ||
                fileExtension.equalsIgnoreCase(FileTypes.JPEG.toString());

        if (!isValidFileExtension) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid file extension. Only " + FileTypes.SVG
                    + "," + FileTypes.PNG + "," + FileTypes.JPEG + " are supported");
        }

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        storageService.store(file, postDto.getFileName());

        Post post = mapToEntity(postDto);
        post.setCategory(category);

        try {
            postRepository.save(post);
        }
        catch (Exception exception) {
            storageService.deleteFileByName(postDto.getFileName());
            throw exception;
        }

        return mapToDto(post);
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
    public SinglePostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Resource resource = storageService.loadAsResource(post.getFileName());

        File file;

        try {
            file = resource.getFile();
        }
        catch (IOException ex) {
            throw new ResourceNotFoundException("File", "id", id);
        }

        String base64String = fileService.getFileBase64String(file);

        if (base64String == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when reading file");
        }

        String fileExtension = fileService.getFileExtension(file.getName());
        String paddedString = fileService.padBase64(base64String, fileExtension);

        SinglePostDto dto = new SinglePostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setContent(post.getContent());
        dto.setCategoryId(post.getCategory().getId());
        dto.setFileBase64(paddedString);

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
        storageService.deleteFileByName(post.getFileName());
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
