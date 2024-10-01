package com.alex.cst323.blogsiteapp.controllers;

import com.alex.cst323.blogsiteapp.models.Post;
import com.alex.cst323.blogsiteapp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	@Autowired
	private PostService postService;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(PostController.class);

	// Get all posts
	@GetMapping
	public List<Post> getAllPosts() {
		logger.info("Entering getAllPosts() method");
		List<Post> posts = postService.findAllPosts();
		logger.info("Exiting getAllPosts() with {} posts found", posts.size());
		return posts;
	}

	// Get a post by ID
	@GetMapping("/{id}")
	public Optional<Post> getPostById(@PathVariable Long id) {
		logger.info("Entering getPostById() with id: {}", id);
		Optional<Post> post = postService.findPostById(id);
		logger.info("Exiting getPostById() with post found: {}", post.isPresent());
		return post;
	}

	// Create a new post
	@PostMapping
	public Post createPost(@RequestBody Post post) {
		logger.info("Entering createPost() with post data: {}", post);
		Post savedPost = postService.savePost(post);
		logger.info("Exiting createPost() with created post ID: {}", savedPost.getPostId());
		return savedPost;
	}

	// Update an existing post
	@PutMapping("/{id}")
	public Post updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
		logger.info("Entering updatePost() with id: {} and post data: {}", id, updatedPost);
		Optional<Post> existingPost = postService.findPostById(id);
		if (existingPost.isPresent()) {
			// Set the post ID from the path variable to ensure the correct post is updated
			updatedPost.setPostId(id);
			Post savedPost = postService.savePost(updatedPost);
			logger.info("Exiting updatePost() with updated post ID: {}", savedPost.getPostId());
			return savedPost;
		} else {
			logger.error("Post not found with id: {}", id);
			return null; // Handle properly, e.g., return 404 Not Found
		}
	}

	// Delete a post by ID
	@DeleteMapping("/{id}")
	public void deletePost(@PathVariable Long id) {
		logger.info("Entering deletePost() with id: {}", id);
		postService.deletePostById(id);
		logger.info("Exiting deletePost() with post deleted for id: {}", id);
	}
}
