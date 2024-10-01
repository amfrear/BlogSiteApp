package com.alex.cst323.blogsiteapp.repositories;

import com.alex.cst323.blogsiteapp.models.Post;
import com.alex.cst323.blogsiteapp.models.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByAuthor(User author);
}
