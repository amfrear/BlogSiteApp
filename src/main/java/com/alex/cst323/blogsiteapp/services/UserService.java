package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
	List<User> findAllUsers();

	Optional<User> findUserById(Long id);

	Optional<User> findByEmail(String email);

	User saveUser(User user);

	void deleteUserById(Long id);

	boolean checkCredentials(String email, String password);
}
