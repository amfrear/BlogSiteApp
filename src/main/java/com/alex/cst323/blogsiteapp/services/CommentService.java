package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentService {
	List<Comment> findAllComments();

	Optional<Comment> findCommentById(Long id);

	Comment saveComment(Comment comment);

	void deleteCommentById(Long id);
}
