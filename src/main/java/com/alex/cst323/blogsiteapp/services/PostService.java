package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.Post;
import java.util.List;
import java.util.Optional;

public interface PostService {
	List<Post> findAllPosts();

	Optional<Post> findPostById(Long id);

	Post savePost(Post post);

	void deletePostById(Long id);
}
