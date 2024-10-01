package com.alex.cst323.blogsiteapp.repositories;

import com.alex.cst323.blogsiteapp.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	// Custom query methods (if needed) go here
}
