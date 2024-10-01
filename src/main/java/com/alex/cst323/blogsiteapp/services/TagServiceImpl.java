package com.alex.cst323.blogsiteapp.services;

import com.alex.cst323.blogsiteapp.models.Tag;
import com.alex.cst323.blogsiteapp.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

	@Autowired
	private TagRepository tagRepository;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

	@Override
	public List<Tag> findAllTags() {
		logger.info("Entering findAllTags() method");
		List<Tag> tags = tagRepository.findAll();
		logger.info("Exiting findAllTags() with {} tags found", tags.size());
		return tags;
	}

	@Override
	public Optional<Tag> findTagById(Long id) {
		logger.info("Entering findTagById() with id: {}", id);
		Optional<Tag> tag = tagRepository.findById(id);
		logger.info("Exiting findTagById() with tag found: {}", tag.isPresent());
		return tag;
	}

	@Override
	public Tag saveTag(Tag tag) {
		logger.info("Entering saveTag() with tag data: {}", tag);

		if (tag.getTagId() != null && tagRepository.existsById(tag.getTagId())) {
			logger.info("Updating existing tag with id: {}", tag.getTagId());
			Optional<Tag> existingTag = tagRepository.findById(tag.getTagId());
			if (existingTag.isPresent()) {
				Tag tagToUpdate = existingTag.get();
				tagToUpdate.setName(tag.getName()); // Assuming name is the field you're updating
				logger.info("Exiting saveTag() after updating tag with id: {}", tagToUpdate.getTagId());
				return tagRepository.save(tagToUpdate);
			}
		}

		// For new tag creation
		Tag savedTag = tagRepository.save(tag);
		logger.info("Exiting saveTag() with created tag id: {}", savedTag.getTagId());
		return savedTag;
	}

	@Override
	public void deleteTagById(Long id) {
		logger.info("Entering deleteTagById() with id: {}", id);
		tagRepository.deleteById(id);
		logger.info("Exiting deleteTagById() after deleting tag with id: {}", id);
	}
}
