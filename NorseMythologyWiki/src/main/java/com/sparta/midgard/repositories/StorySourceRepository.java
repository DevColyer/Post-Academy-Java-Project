package com.sparta.midgard.repositories;

import com.sparta.midgard.models.StorySource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorySourceRepository extends JpaRepository<StorySource, Integer> {
}