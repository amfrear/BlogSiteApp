package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.Comment;
import com.alex.cst323.blogsiteapp.models.Post;
import com.alex.cst323.blogsiteapp.models.User;
import com.alex.cst323.blogsiteapp.repositories.CommentRepository;
import com.alex.cst323.blogsiteapp.repositories.PostRepository;
import com.alex.cst323.blogsiteapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

	@Override
	public List<Comment> findAllComments() {
		logger.info("Entering findAllComments() method");
		List<Comment> comments = commentRepository.findAll();
		logger.info("Exiting findAllComments() with {} comments found", comments.size());
		return comments;
	}

	@Override
	public Optional<Comment> findCommentById(Long id) {
		logger.info("Entering findCommentById() with id: {}", id);
		Optional<Comment> comment = commentRepository.findById(id);
		logger.info("Exiting findCommentById() with comment found: {}", comment.isPresent());
		return comment;
	}

	@Override
	public Comment saveComment(Comment comment) {
		logger.info("Entering saveComment() with comment data: {}", comment);

		if (comment.getCommentId() != null && commentRepository.existsById(comment.getCommentId())) {
			// Updating existing comment
			logger.info("Updating existing comment with id: {}", comment.getCommentId());
			Optional<Comment> existingComment = commentRepository.findById(comment.getCommentId());

			if (existingComment.isPresent()) {
				Comment commentToUpdate = existingComment.get();
				commentToUpdate.setContent(comment.getContent());
				commentToUpdate.setUpdatedAt(LocalDateTime.now());

				// Fetch the related Post and User entities before saving the Comment
				Optional<Post> post = postRepository.findById(comment.getPost().getPostId());
				Optional<User> author = userRepository.findById(comment.getAuthor().getUserId());

				if (post.isPresent() && author.isPresent()) {
					commentToUpdate.setPost(post.get());
					commentToUpdate.setAuthor(author.get());
				} else {
					logger.error("Post or Author not found for comment with id: {}", comment.getCommentId());
					throw new RuntimeException("Post or Author not found");
				}

				logger.info("Exiting saveComment() after updating comment with id: {}", commentToUpdate.getCommentId());
				return commentRepository.save(commentToUpdate);
			}
		}

		// For new comment creation
		if (comment.getCreatedAt() == null) {
			comment.setCreatedAt(LocalDateTime.now());
		}
		comment.setUpdatedAt(LocalDateTime.now());

		// Fetch the related Post and User entities before saving the Comment
		Optional<Post> post = postRepository.findById(comment.getPost().getPostId());
		Optional<User> author = userRepository.findById(comment.getAuthor().getUserId());

		if (post.isPresent() && author.isPresent()) {
			comment.setPost(post.get());
			comment.setAuthor(author.get());
		} else {
			logger.error("Post or Author not found for new comment");
			throw new RuntimeException("Post or Author not found");
		}

		Comment savedComment = commentRepository.save(comment);
		logger.info("Exiting saveComment() with created comment id: {}", savedComment.getCommentId());
		return savedComment;
	}

	@Override
	public void deleteCommentById(Long id) {
		logger.info("Entering deleteCommentById() with id: {}", id);
		commentRepository.deleteById(id);
		logger.info("Exiting deleteCommentById() after deleting comment with id: {}", id);
	}
}
