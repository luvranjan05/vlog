package com.vlogexample.ranvlog.controller;

import com.vlogexample.ranvlog.entity.Comment;
import com.vlogexample.ranvlog.entity.Post;
import com.vlogexample.ranvlog.repository.CommentRepository;
import com.vlogexample.ranvlog.repository.PostRepository;
import com.vlogexample.ranvlog.service.FileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FileUploadService fileUploadService;

    public PostController(PostRepository postRepository, CommentRepository commentRepository, FileUploadService fileUploadService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam("header") String header,
            @RequestParam("paragraph") String paragraph,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            if (photo != null && photo.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }

            String photoPath = (photo != null && !photo.isEmpty()) ? fileUploadService.saveFile(photo) : null;
            Post post = new Post();
            post.setHeader(header);
            post.setParagraph(paragraph);
            post.setPhoto(photoPath);

            return ResponseEntity.status(HttpStatus.CREATED).body(postRepository.save(post));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestParam("header") String header,
            @RequestParam("paragraph") String paragraph,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            Post existingPost = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
            existingPost.setHeader(header);
            existingPost.setParagraph(paragraph);

            if (photo != null && !photo.isEmpty()) {
                String photoPath = fileUploadService.saveFile(photo);
                existingPost.setPhoto(photoPath);
            }

            return ResponseEntity.ok(postRepository.save(existingPost));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        try {
            byte[] imageData = fileUploadService.getFile(fileName);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long id) {
        return postRepository.findById(id).map(post -> {
            post.incrementLikes();
            return ResponseEntity.ok(postRepository.save(post));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(post -> ResponseEntity.ok(post.getComments()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long id, @RequestBody Comment comment) {
        return postRepository.findById(id).map(post -> {
            post.addComment(comment);
            commentRepository.save(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}