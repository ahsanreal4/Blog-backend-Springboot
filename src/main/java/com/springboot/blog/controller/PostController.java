package com.springboot.blog.controller;

import com.springboot.blog.dto.post.PostDto;
import com.springboot.blog.dto.post.PostResponseDto;
import com.springboot.blog.dto.post.SinglePostDto;
import com.springboot.blog.service.PostService;
import com.springboot.blog.service.StorageService;
import com.springboot.blog.utils.constants.PostConstants;
import com.springboot.blog.utils.constants.UserRoles;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@SecurityRequirement(
        name = "Bear Authentication"
)
@Tag(
        name = "Post"
)
public class PostController {

    private final PostService postService;

    public PostController(PostService postService, StorageService storageService) {
        this.postService = postService;
    }

    // create blog post
    @PreAuthorize(UserRoles.HAS_ROLE_ADMIN)
    @PostMapping()
    public ResponseEntity<PostDto> createPost(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("content") String content,
            @RequestParam("categoryId") long categoryId
    )
    {
        String nowTime = Long.toString(new Date().getTime());
        String fileName = nowTime + "_" + file.getOriginalFilename();

        PostDto dto = new PostDto();
        dto.setCategoryId(categoryId);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setDescription(description);
        dto.setFileName(fileName);

        PostDto created = postService.createPost(dto, file);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // get all blog posts
    @GetMapping()
    public PostResponseDto getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = PostConstants.DEFAULT_PAGE_NO, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = PostConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = PostConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = PostConstants.DEFAULT_SORT_DIR, required = false) String sortDir
    ){
        return postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
    }

    // get post by id
    @GetMapping("/{id}")
    public ResponseEntity<SinglePostDto> getPostById(@PathVariable(name = "id") long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // update post by id
    @PreAuthorize(UserRoles.HAS_ROLE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, @PathVariable(name = "id") long id){
        return new ResponseEntity<>(postService.updatePost(postDto, id), HttpStatus.OK);
    }

    // delete post by id
    @PreAuthorize(UserRoles.HAS_ROLE_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") long id){
        postService.deletePost(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }

    // get posts by category id
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategoryId(@PathVariable(name = "categoryId") long categoryId){
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId));
    }
}
