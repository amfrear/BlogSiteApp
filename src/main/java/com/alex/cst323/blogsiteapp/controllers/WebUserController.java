package com.alex.cst323.blogsiteapp.controllers;

import com.alex.cst323.blogsiteapp.models.Comment;
import com.alex.cst323.blogsiteapp.models.Post;
import com.alex.cst323.blogsiteapp.models.User;
import com.alex.cst323.blogsiteapp.repositories.CommentRepository;
import com.alex.cst323.blogsiteapp.repositories.PostRepository;
import com.alex.cst323.blogsiteapp.repositories.UserRepository;
import com.alex.cst323.blogsiteapp.services.PostService;
import com.alex.cst323.blogsiteapp.services.UserService;
import com.alex.cst323.blogsiteapp.models.Tag;
import com.alex.cst323.blogsiteapp.repositories.TagRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class WebUserController {

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private CommentRepository commentRepository;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(WebUserController.class);

	@GetMapping("/home")
	public String showHomePage(Model model) {
		logger.info("Entering showHomePage() method");
		List<Post> posts = postService.findAllPosts();
		logger.info("Number of posts found: {}", posts.size());
		model.addAttribute("posts", posts);
		logger.info("Exiting showHomePage()");
		return "home"; // This will return home.html
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		logger.info("Entering showRegistrationForm() method");
		model.addAttribute("user", new User());
		logger.info("Exiting showRegistrationForm()");
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute("user") User user, Model model) {
		logger.info("Entering registerUser() with user: {}", user);
		userService.saveUser(user);
		logger.info("User registered with email: {}", user.getEmail());
		return "redirect:/login";
	}

	@GetMapping("/profile")
	public String showProfile(Model model, HttpSession session) {
		logger.info("Entering showProfile() method");
		String email = (String) session.getAttribute("loggedInUserEmail");

		if (email == null) {
			logger.warn("No user logged in; redirecting to /login");
			return "redirect:/login";
		}

		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			model.addAttribute("user", user);
			List<Post> userPosts = postRepository.findByAuthor(user);
			logger.info("Number of posts found for user: {}", userPosts.size());
			model.addAttribute("posts", userPosts);
			return "profile";
		}

		logger.warn("User not found; redirecting to /login");
		return "redirect:/login";
	}

	@PostMapping("/post/create")
	public String createPost(@RequestParam String title, @RequestParam String content, @RequestParam String tags,
			HttpSession session) {
		logger.info("Entering createPost() with title: {}, content length: {}, tags: {}", title, content.length(),
				tags);
		String email = (String) session.getAttribute("loggedInUserEmail");

		if (email == null) {
			logger.warn("No user logged in; redirecting to /login");
			return "redirect:/login";
		}

		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isPresent()) {
			User author = optionalUser.get();

			// Create a new Post object
			Post post = new Post();
			post.setTitle(title);
			post.setContent(content);
			post.setAuthor(author);
			post.setCreatedAt(LocalDateTime.now());

			// Process the tags input
			if (!tags.isEmpty()) {
				List<Tag> tagList = Arrays.stream(tags.split(",")).map(String::trim).map(tagName -> {
					Tag existingTag = tagRepository.findByName(tagName);
					if (existingTag == null) {
						// Create a new tag using the appropriate constructor or setter
						Tag newTag = new Tag();
						newTag.setName(tagName);
						return tagRepository.save(newTag);
					} else {
						return existingTag;
					}
				}).collect(Collectors.toList());
				post.setTags(tagList);
			}

			postService.savePost(post);
			logger.info("Post created with title: {}", post.getTitle());
		}

		return "redirect:/profile";
	}

	@GetMapping("/profile/edit")
	public String showEditProfileForm(Model model, HttpSession session) {
		logger.info("Entering showEditProfileForm()");
		String email = (String) session.getAttribute("loggedInUserEmail");

		if (email == null) {
			logger.warn("No user logged in; redirecting to /login");
			return "redirect:/login";
		}

		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			model.addAttribute("user", user);
			logger.info("Exiting showEditProfileForm()");
			return "edit-profile";
		}

		logger.warn("User not found; redirecting to /login");
		return "redirect:/login";
	}

	@PostMapping("/profile/update")
	public String updateProfile(@ModelAttribute("user") User user, BindingResult result, Model model) {
		logger.info("Entering updateProfile() with user: {}", user);
		User existingUser = userRepository.findById(user.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

		user.setCreatedAt(existingUser.getCreatedAt());
		user.setUpdatedAt(LocalDateTime.now());
		user.setPosts(existingUser.getPosts());

		if (user.getPassword() == null || user.getPassword().isEmpty()) {
			user.setPassword(existingUser.getPassword());
		}

		userRepository.save(user);
		logger.info("Profile updated for user with email: {}", user.getEmail());
		return "redirect:/profile";
	}

	@GetMapping("/profile/post/edit/{postId}")
	public String editPostForm(@PathVariable Long postId, Model model) {
		logger.info("Entering editPostForm() with postId: {}", postId);
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
		model.addAttribute("post", post);
		logger.info("Exiting editPostForm() with post title: {}", post.getTitle());
		return "edit-post";
	}

	@PostMapping("/profile/post/update")
	public String updatePost(@ModelAttribute("post") Post post, BindingResult result) {
		logger.info("Entering updatePost() with post: {}", post);
		if (result.hasErrors()) {
			logger.warn("Validation errors occurred in updatePost()");
			return "edit-post";
		}

		Post existingPost = postRepository.findById(post.getPostId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

		post.setAuthor(existingPost.getAuthor());
		post.setCreatedAt(existingPost.getCreatedAt());
		post.setComments(existingPost.getComments());
		post.setUpdatedAt(LocalDateTime.now());

		postRepository.save(post);
		logger.info("Post updated with title: {}", post.getTitle());
		return "redirect:/profile";
	}

	@GetMapping("/profile/post/delete/{postId}")
	public String deletePost(@PathVariable Long postId, HttpSession session) {
		logger.info("Entering deletePost() with postId: {}", postId);
		String email = (String) session.getAttribute("loggedInUserEmail");

		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

		if (!post.getAuthor().getEmail().equals(email)) {
			logger.warn("Logged-in user is not the author of postId: {}", postId);
			return "redirect:/profile";
		}

		postRepository.delete(post);
		logger.info("Post deleted with postId: {}", postId);
		return "redirect:/profile";
	}

	@GetMapping("/posts/{postId}")
	public String showPostDetails(@PathVariable Long postId, Model model) {
		logger.info("Entering showPostDetails() with postId: {}", postId);
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
		model.addAttribute("post", post);
		logger.info("Exiting showPostDetails() with post title: {}", post.getTitle());
		return "post-details";
	}

	@PostMapping("/posts/{postId}/comments/add")
	public String addComment(@PathVariable Long postId, @RequestParam String content, HttpSession session) {
		logger.info("Entering addComment() for postId: {}, content length: {}", postId, content.length());
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

		String email = (String) session.getAttribute("loggedInUserEmail");
		User author = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

		Comment comment = new Comment();
		comment.setContent(content);
		comment.setCreatedAt(LocalDateTime.now());
		comment.setPost(post);
		comment.setAuthor(author);

		commentRepository.save(comment);
		logger.info("Comment added to postId: {}", postId);
		return "redirect:/posts/" + postId;
	}

	@GetMapping("/posts/{postId}/comments/edit/{commentId}")
	public String showEditCommentForm(@PathVariable Long postId, @PathVariable Long commentId, Model model,
			HttpSession session) {
		logger.info("Entering showEditCommentForm() for commentId: {}", commentId);
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

		String loggedInEmail = (String) session.getAttribute("loggedInUserEmail");
		if (!comment.getAuthor().getEmail().equals(loggedInEmail)) {
			logger.warn("Logged-in user is not the author of commentId: {}", commentId);
			return "redirect:/posts/" + postId;
		}

		model.addAttribute("comment", comment);
		model.addAttribute("postId", postId);
		logger.info("Exiting showEditCommentForm()");
		return "edit-comment";
	}

	@PostMapping("/posts/{postId}/comments/update/{commentId}")
	public String updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestParam String content,
			HttpSession session) {
		logger.info("Entering updateComment() for commentId: {}, postId: {}", commentId, postId);
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

		String loggedInEmail = (String) session.getAttribute("loggedInUserEmail");
		if (!comment.getAuthor().getEmail().equals(loggedInEmail)) {
			logger.warn("Logged-in user is not the author of commentId: {}", commentId);
			return "redirect:/posts/" + postId;
		}

		comment.setContent(content);
		comment.setUpdatedAt(LocalDateTime.now());

		commentRepository.save(comment);
		logger.info("Comment updated for commentId: {}", commentId);
		return "redirect:/posts/" + postId;
	}

	@PostMapping("/posts/{postId}/comments/{commentId}/delete")
	public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpSession session) {
		logger.info("Entering deleteComment() for commentId: {}, postId: {}", commentId, postId);
		String email = (String) session.getAttribute("loggedInUserEmail");
		User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

		if (!comment.getAuthor().equals(user)) {
			logger.warn("Logged-in user is not the author of commentId: {}", commentId);
			throw new IllegalArgumentException("You are not authorized to delete this comment");
		}

		commentRepository.delete(comment);
		logger.info("Comment deleted for commentId: {}", commentId);
		return "redirect:/posts/" + postId;
	}
}
