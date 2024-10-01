package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.User;
import com.alex.cst323.blogsiteapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public List<User> findAllUsers() {
		logger.info("Entering findAllUsers() method");
		List<User> users = userRepository.findAll();
		logger.info("Exiting findAllUsers() with {} users found", users.size());
		return users;
	}

	@Override
	public Optional<User> findUserById(Long id) {
		logger.info("Entering findUserById() with id: {}", id);
		Optional<User> user = userRepository.findById(id);
		logger.info("Exiting findUserById() with user found: {}", user.isPresent());
		return user;
	}

	@Override
	public Optional<User> findByEmail(String email) {
		logger.info("Entering findByEmail() with email: {}", email);
		Optional<User> user = userRepository.findByEmail(email);
		logger.info("Exiting findByEmail() with user found: {}", user.isPresent());
		return user;
	}

	@Override
	public User saveUser(User user) {
		logger.info("Entering saveUser() with user data: {}", user);

		// Check if updating an existing user
		if (user.getUserId() != null && userRepository.existsById(user.getUserId())) {
			logger.info("Updating existing user with id: {}", user.getUserId());
			Optional<User> existingUser = userRepository.findById(user.getUserId());
			if (existingUser.isPresent()) {
				// Check if the updated email is already in use by another user
				Optional<User> userWithSameEmail = userRepository.findByEmail(user.getEmail());
				if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getUserId().equals(user.getUserId())) {
					logger.error("Email {} is already in use by another user", user.getEmail());
					throw new RuntimeException("Email is already in use by another user");
				}

				// Proceed with the update
				User userToUpdate = existingUser.get();
				userToUpdate.setUsername(user.getUsername());
				userToUpdate.setEmail(user.getEmail());
				userToUpdate.setPassword(user.getPassword());
				userToUpdate.setUpdatedAt(LocalDateTime.now()); // Set updated timestamp
				logger.info("Exiting saveUser() after updating user with id: {}", userToUpdate.getUserId());
				return userRepository.save(userToUpdate);
			}
		}

		// For new user creation
		if (user.getCreatedAt() == null) {
			user.setCreatedAt(LocalDateTime.now());
		}
		user.setUpdatedAt(LocalDateTime.now());
		User savedUser = userRepository.save(user);
		logger.info("Exiting saveUser() with created user id: {}", savedUser.getUserId());
		return savedUser;
	}

	@Override
	public void deleteUserById(Long id) {
		logger.info("Entering deleteUserById() with id: {}", id);
		userRepository.deleteById(id);
		logger.info("Exiting deleteUserById() after deleting user with id: {}", id);
	}

	@Override
	public boolean checkCredentials(String email, String password) {
		logger.info("Entering checkCredentials() with email: {}", email);
		Optional<User> userOpt = userRepository.findByEmail(email);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			boolean isValid = user.getPassword().equals(password); // Simplistic password check
			logger.info("Exiting checkCredentials() with valid credentials: {}", isValid);
			return isValid;
		}
		logger.info("Exiting checkCredentials() with user not found");
		return false;
	}
}
