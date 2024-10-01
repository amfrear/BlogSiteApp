package com.alex.cst323.blogsiteapp.controllers;

import com.alex.cst323.blogsiteapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	// GET request to display the login page
	@GetMapping("/login")
	public String login() {
		logger.info("Entering login() method");
		return "login"; // Should return login.html
	}

	// POST request to handle login
	@PostMapping("/login")
	public String loginUser(@RequestParam String email, @RequestParam String password, Model model,
			HttpSession session) {
		logger.info("Entering loginUser() method with email: {}", email);

		if (userService.checkCredentials(email, password)) {
			// Store the authenticated user's email in the session
			session.setAttribute("loggedInUserEmail", email);
			logger.info("User authenticated successfully with email: {}", email);
			// Redirect to home after successful login
			return "redirect:/home";
		}

		// If credentials are invalid, add error attribute and return login page
		model.addAttribute("error", true);
		logger.warn("Authentication failed for email: {}", email);
		return "login";
	}

	// GET request to handle logout
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		logger.info("Entering logout() method");
		// Invalidate the session to clear the user's session data
		session.invalidate();
		logger.info("User logged out successfully");
		// Redirect to the login page after logout
		return "redirect:/login";
	}
}
