package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.Tag;
import java.util.List;
import java.util.Optional;

public interface TagService {
	List<Tag> findAllTags();

	Optional<Tag> findTagById(Long id);

	Tag saveTag(Tag tag);

	void deleteTagById(Long id);
}
