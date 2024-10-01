package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.Post;
import com.alex.cst323.blogsiteapp.models.User;
import com.alex.cst323.blogsiteapp.models.Tag;
import com.alex.cst323.blogsiteapp.repositories.PostRepository;
import com.alex.cst323.blogsiteapp.repositories.UserRepository;
import com.alex.cst323.blogsiteapp.repositories.TagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TagRepository tagRepository;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

	@Override
	public List<Post> findAllPosts() {
		logger.info("Entering findAllPosts() method");
		List<Post> posts = postRepository.findAll();
		logger.info("Posts found: {}", posts.size());
		posts.forEach(post -> logger.debug("Post Title: {}, Content: {}", post.getTitle(), post.getContent()));
		logger.info("Exiting findAllPosts()");
		return posts;
	}

	@Override
	public Optional<Post> findPostById(Long id) {
		logger.info("Entering findPostById() with id: {}", id);
		Optional<Post> post = postRepository.findById(id);
		logger.info("Exiting findPostById() with post found: {}", post.isPresent());
		return post;
	}

	@Override
	public Post savePost(Post post) {
		logger.info("Entering savePost() with post data: {}", post);

		// Fetch full User object based on author ID
		Optional<User> author = userRepository.findById(post.getAuthor().getUserId());
		author.ifPresent(post::setAuthor);
		logger.debug("Post author set to: {}", post.getAuthor());

		// Fetch full Tag objects based on provided Tag IDs
		List<Tag> fullTags = new ArrayList<>();
		for (Tag tag : post.getTags()) {
			Optional<Tag> fullTag = tagRepository.findById(tag.getTagId());
			fullTag.ifPresent(fullTags::add);
		}
		post.setTags(fullTags);
		logger.debug("Post tags set to: {}", post.getTags());

		// Check if this is an update operation
		if (post.getPostId() != null && postRepository.existsById(post.getPostId())) {
			logger.info("Updating existing post with id: {}", post.getPostId());
			Optional<Post> existingPost = postRepository.findById(post.getPostId());
			if (existingPost.isPresent()) {
				Post postToUpdate = existingPost.get();
				postToUpdate.setTitle(post.getTitle());
				postToUpdate.setContent(post.getContent());
				postToUpdate.setUpdatedAt(LocalDateTime.now());
				postToUpdate.setTags(post.getTags());
				postToUpdate.setAuthor(post.getAuthor()); // Update author if necessary
				logger.info("Exiting savePost() after updating post with id: {}", postToUpdate.getPostId());
				return postRepository.save(postToUpdate);
			}
		}

		// If not an update, treat it as a new post creation
		if (post.getCreatedAt() == null) {
			post.setCreatedAt(LocalDateTime.now());
		}
		post.setUpdatedAt(LocalDateTime.now());
		Post savedPost = postRepository.save(post);
		logger.info("Exiting savePost() with created post id: {}", savedPost.getPostId());
		return savedPost;
	}

	@Override
	public void deletePostById(Long id) {
		logger.info("Entering deletePostById() with id: {}", id);
		postRepository.deleteById(id);
		logger.info("Exiting deletePostById() after deleting post with id: {}", id);
	}
}
