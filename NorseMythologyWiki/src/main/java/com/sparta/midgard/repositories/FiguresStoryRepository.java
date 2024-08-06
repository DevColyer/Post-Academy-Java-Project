package com.sparta.midgard.repositories;

import com.sparta.midgard.models.FiguresStory;
import com.sparta.midgard.models.FiguresStoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface FiguresStoryRepository extends JpaRepository<FiguresStory, FiguresStoryId> {
    Stream<FiguresStory> findBy();
}