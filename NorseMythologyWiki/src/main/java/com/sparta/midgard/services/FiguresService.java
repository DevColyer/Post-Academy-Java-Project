package com.sparta.midgard.services;

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

    public boolean createFigure(Figure figure) throws Exception {

        if (figureRepository.existsById(figure.getId())) {
            return false;
        }
        figureRepository.save(figure);
        return true;
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
        Optional<Story> searchStory = storyRepository.findByName(storyName);

        return searchStory.isPresent() ?
                figuresStoryRepository.findBy()
                        .filter(figureStory ->
                                figureStory.getStory().equals(searchStory.get()))
                        .map(FiguresStory::getFigure)
                : Stream.empty();
    }

    public boolean updateFigure(int id, String name, String imageLink) {
        Optional<Figure> figureOptional = figureRepository.findById(id);
        if (figureOptional.isPresent()) {
            Figure figure = figureOptional.get();
            if (name != null && !name.isEmpty()) {
                figure.setName(name);
            }
            if (imageLink != null && !imageLink.isEmpty()) {
                figure.setImageLink(imageLink);
            }
            figureRepository.save(figure);
            return true;
        }
        throw new ResourceNotFoundException("Figure: " + id + " not found.");
    }

    public boolean deleteFigure(int id) {
        Optional<Figure> figureOptional = figureRepository.findById(id);
        if (figureOptional.isPresent()) {
            Figure figure = figureOptional.get();
            figureRepository.delete(figure);
            return true;
        }
        throw new ResourceNotFoundException("Figure: " + id + " not found.");
    }
}
