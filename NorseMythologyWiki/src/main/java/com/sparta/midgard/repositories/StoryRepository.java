package com.sparta.midgard.repositories;

import com.sparta.midgard.models.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface StoryRepository extends JpaRepository<Story, Integer> {
    Stream<Story> findBy();
    Optional<Story> findByName(String storyName);
}