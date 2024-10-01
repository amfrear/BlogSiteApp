package com.alex.cst323.blogsiteapp.controllers;

import com.alex.cst323.blogsiteapp.models.Comment;
import com.alex.cst323.blogsiteapp.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

	// Get all comments
	@GetMapping
	public List<Comment> getAllComments() {
		logger.info("Entering getAllComments() method");
		List<Comment> comments = commentService.findAllComments();
		logger.info("Exiting getAllComments() with {} comments found", comments.size());
		return comments;
	}

	// Get a comment by ID
	@GetMapping("/{id}")
	public Optional<Comment> getCommentById(@PathVariable Long id) {
		logger.info("Entering getCommentById() with id: {}", id);
		Optional<Comment> comment = commentService.findCommentById(id);
		logger.info("Exiting getCommentById() with comment found: {}", comment.isPresent());
		return comment;
	}

	// Create a new comment
	@PostMapping
	public Comment createComment(@RequestBody Comment comment) {
		logger.info("Entering createComment() with comment data: {}", comment);
		Comment savedComment = commentService.saveComment(comment);
		logger.info("Exiting createComment() with created comment ID: {}", savedComment.getCommentId());
		return savedComment;
	}

	@PutMapping("/{id}")
	public Comment updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
		logger.info("Entering updateComment() with id: {} and comment data: {}", id, updatedComment);
		Optional<Comment> existingComment = commentService.findCommentById(id);
		if (existingComment.isPresent()) {
			updatedComment.setCommentId(id); // Ensure the correct ID is used for updating
			Comment savedComment = commentService.saveComment(updatedComment);
			logger.info("Exiting updateComment() with updated comment ID: {}", savedComment.getCommentId());
			return savedComment;
		} else {
			logger.error("Comment not found with id: {}", id);
			throw new RuntimeException("Comment not found");
		}
	}

	// Delete a comment by ID
	@DeleteMapping("/{id}")
	public void deleteComment(@PathVariable Long id) {
		logger.info("Entering deleteComment() with id: {}", id);
		commentService.deleteCommentById(id);
		logger.info("Exiting deleteComment() with comment deleted for id: {}", id);
	}
}
