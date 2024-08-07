package com.sparta.midgard.services;

import com.sparta.midgard.exceptions.ResourceAlreadyExistsException;
import com.sparta.midgard.exceptions.ResourceNotFoundException;
import com.sparta.midgard.models.Figure;
import com.sparta.midgard.models.FiguresStory;
import com.sparta.midgard.models.Story;
import com.sparta.midgard.repositories.FigureRepository;
import com.sparta.midgard.repositories.FiguresStoryRepository;
import com.sparta.midgard.repositories.StoryRepository;
import com.sparta.midgard.utils.StaticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FiguresService {
    private final FigureRepository figureRepository;
    private final FiguresStoryRepository figuresStoryRepository;
    private final StoryRepository storyRepository;

    @Autowired
    public FiguresService(FigureRepository figureRepository, FiguresStoryRepository figuresStoryRepository, StoryRepository storyRepository) {
        this.figureRepository = figureRepository;
        this.figuresStoryRepository = figuresStoryRepository;
        this.storyRepository = storyRepository;
    }

    public Optional<Figure> createFigure(Figure figure) {

        if (figureRepository.existsById(figure.getId())) {
            throw new ResourceAlreadyExistsException("Figure already exists.");
        }
        figureRepository.save(figure);
        return figureRepository.findById(figure.getId());
    }

    public List<Figure> getFigures() {
        return figureRepository.findAll();
    }

    public Optional<Figure> getFigureById(int id) {
        return figureRepository.findById(id);
    }

    public Stream<Figure> searchFiguresByName(String name) {
        String regex = StaticUtils.getPartialMatcherRegex(name);

        return figureRepository.findBy()
                .filter(figure -> figure.getName().matches(regex));
    }

    public Stream<Figure> getFiguresByStory(String storyName) {
        String regex = StaticUtils.getPartialMatcherRegex(storyName);

        return storyRepository.findBy()
                .filter(story -> story.getName().matches(regex))
                .flatMap(matchedStory ->
                        figuresStoryRepository.findBy()
                                .filter(figureStory -> figureStory.getStory().equals(matchedStory))
                                .map(FiguresStory::getFigure));
    }

    public Optional<Figure> updateFigure(int id, String name, String imageLink) {
        Optional<Figure> figureOptional = figureRepository.findById(id);
        if (figureOptional.isPresent()) {
            Figure figure = figureOptional.get();
            if (name != null && !name.isEmpty()) {
                figure.setName(name);
            }
            if (imageLink != null && !imageLink.isEmpty()) {
                figure.setImageLink(imageLink);
            }

            return Optional.of(figureRepository.save(figure));
        }
        throw new ResourceNotFoundException("Figure: " + id + " not found.");
    }

    public Optional<Figure> deleteFigure(int id) {
        Optional<Figure> figureOptional = figureRepository.findById(id);
        if (figureOptional.isPresent()) {
            Figure figure = figureOptional.get();
            figureRepository.delete(figure);
            return Optional.empty();
        }
        throw new ResourceNotFoundException("Figure: " + id + " not found.");
    }
}
