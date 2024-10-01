package com.alex.cst323.blogsiteapp.controllers;

import com.alex.cst323.blogsiteapp.models.User;
import com.alex.cst323.blogsiteapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	// Get all users
	@GetMapping
	public List<User> getAllUsers() {
		logger.info("Entering getAllUsers() method");
		List<User> users = userService.findAllUsers();
		logger.info("Exiting getAllUsers() with {} users found", users.size());
		return users;
	}

	// Get a user by ID
	@GetMapping("/{id}")
	public Optional<User> getUserById(@PathVariable Long id) {
		logger.info("Entering getUserById() with id: {}", id);
		Optional<User> user = userService.findUserById(id);
		logger.info("Exiting getUserById() with user found: {}", user.isPresent());
		return user;
	}

	// Create a new user
	@PostMapping
	public User createUser(@RequestBody User user) {
		logger.info("Entering createUser() with user data: {}", user);
		User savedUser = userService.saveUser(user);
		logger.info("Exiting createUser() with created user ID: {}", savedUser.getUserId());
		return savedUser;
	}

	// Update an existing user
	@PutMapping("/{id}")
	public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
		logger.info("Entering updateUser() with id: {} and user data: {}", id, updatedUser);
		Optional<User> existingUser = userService.findUserById(id);
		if (existingUser.isPresent()) {
			// Set the ID to ensure we update the correct user
			updatedUser.setUserId(id);
			User savedUser = userService.saveUser(updatedUser);
			logger.info("Exiting updateUser() with updated user ID: {}", savedUser.getUserId());
			return savedUser;
		} else {
			logger.error("User not found with id: {}", id);
			throw new RuntimeException("User not found"); // Better to throw 404 or handle appropriately
		}
	}

	// Delete a user by ID
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		logger.info("Entering deleteUser() with id: {}", id);
		userService.deleteUserById(id);
		logger.info("Exiting deleteUser() with user deleted for id: {}", id);
	}
}
