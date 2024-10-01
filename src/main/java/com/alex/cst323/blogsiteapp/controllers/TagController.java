package com.alex.cst323.blogsiteapp.controllers;

import com.alex.cst323.blogsiteapp.models.Tag;
import com.alex.cst323.blogsiteapp.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {

	@Autowired
	private TagService tagService;

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(TagController.class);

	// Get all tags
	@GetMapping
	public List<Tag> getAllTags() {
		logger.info("Entering getAllTags() method");
		List<Tag> tags = tagService.findAllTags();
		logger.info("Exiting getAllTags() with {} tags found", tags.size());
		return tags;
	}

	// Get a tag by ID
	@GetMapping("/{id}")
	public Optional<Tag> getTagById(@PathVariable Long id) {
		logger.info("Entering getTagById() with id: {}", id);
		Optional<Tag> tag = tagService.findTagById(id);
		logger.info("Exiting getTagById() with tag found: {}", tag.isPresent());
		return tag;
	}

	// Create a new tag
	@PostMapping
	public Tag createTag(@RequestBody Tag tag) {
		logger.info("Entering createTag() with tag data: {}", tag);
		Tag savedTag = tagService.saveTag(tag);
		logger.info("Exiting createTag() with created tag ID: {}", savedTag.getTagId());
		return savedTag;
	}

	// Update an existing tag
	@PutMapping("/{id}")
	public Tag updateTag(@PathVariable Long id, @RequestBody Tag updatedTag) {
		logger.info("Entering updateTag() with id: {} and tag data: {}", id, updatedTag);
		Optional<Tag> existingTag = tagService.findTagById(id);
		if (existingTag.isPresent()) {
			updatedTag.setTagId(id); // Make sure to set the correct ID
			Tag savedTag = tagService.saveTag(updatedTag);
			logger.info("Exiting updateTag() with updated tag ID: {}", savedTag.getTagId());
			return savedTag;
		} else {
			logger.error("Tag not found with id: {}", id);
			return null; // Handle properly, e.g., return 404 Not Found
		}
	}

	// Delete a tag by ID
	@DeleteMapping("/{id}")
	public void deleteTag(@PathVariable Long id) {
		logger.info("Entering deleteTag() with id: {}", id);
		tagService.deleteTagById(id);
		logger.info("Exiting deleteTag() with tag deleted for id: {}", id);
	}
}
