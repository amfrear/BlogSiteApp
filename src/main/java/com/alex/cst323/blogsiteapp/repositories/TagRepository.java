package com.alex.cst323.blogsiteapp.repositories;

import com.alex.cst323.blogsiteapp.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
	 Tag findByName(String name);
}
