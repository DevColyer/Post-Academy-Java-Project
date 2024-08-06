package com.sparta.midgard.repositories;

import com.sparta.midgard.models.Figure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface FigureRepository extends JpaRepository<Figure, Integer> {
    Optional<Figure> findByName(String figureName);
    Stream<Figure> findBy();
}