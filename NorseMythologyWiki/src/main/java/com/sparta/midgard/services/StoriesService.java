package com.sparta.midgard.services;

import com.sparta.midgard.exceptions.ResourceNotFoundException;
import com.sparta.midgard.models.FiguresStory;
import com.sparta.midgard.models.Story;
import com.sparta.midgard.repositories.FigureRepository;
import com.sparta.midgard.repositories.FiguresStoryRepository;
import com.sparta.midgard.repositories.StoryRepository;
import com.sparta.midgard.repositories.StorySourceRepository;
import com.sparta.midgard.utils.StaticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class StoriesService {
    private final FigureRepository figureRepository;
    private final FiguresStoryRepository figuresStoryRepository;
    private final StoryRepository storyRepository;
    private final StorySourceRepository storySourceRepository;

    @Autowired
    public StoriesService(FigureRepository figureRepository, FiguresStoryRepository figuresStoryRepository, StoryRepository storyRepository, StorySourceRepository storySourceRepository) {
        this.figureRepository = figureRepository;
        this.figuresStoryRepository = figuresStoryRepository;
        this.storyRepository = storyRepository;
        this.storySourceRepository = storySourceRepository;
    }

    public Optional<Story> createStory(Story story) {
        if (storyRepository.existsById(story.getId())) {
            return Optional.empty();
        }

        return Optional.of(storyRepository.save(story));
    }

    public List<Story> getStories() {
        return storyRepository.findAll();
    }

    public Optional<Story> getStoryById(int id) {
        return storyRepository.findById(id);
    }

    public Stream<Story> searchStoryByName(String name) {
        String regex = StaticUtils.getPartialMatcherRegex(name);

        return storyRepository.findBy()
                .filter(story -> story.getName().matches(regex));
    }

    public Stream<Story> searchStoriesByFigure(String figureName) {
        String regex = StaticUtils.getPartialMatcherRegex(figureName);

        return figureRepository.findBy()
                .filter(figure -> figure.getName().matches(regex))
                .flatMap(matchedFigure ->
                        figuresStoryRepository.findBy()
                                .filter(figureStory -> figureStory.getFigure().equals(matchedFigure))
                                .map(FiguresStory::getStory));
        }

    public Optional<Story> updateStory(int id, String name, int sourceId){
        Optional<Story> storyOptional = storyRepository.findById(id);
        if (storyOptional.isPresent()) {
            Story figure = storyOptional.get();
            if (name != null && !name.isEmpty()) {
                figure.setName(name);
            }
            if (storySourceRepository.existsById(sourceId)) {
                figure.setSource(storySourceRepository.findById(sourceId).get());
            }

            return Optional.of(storyRepository.save(figure));
        }
        throw new ResourceNotFoundException("Story: " + id + " not found.");
    }

    public Optional<Story> deleteStory(int id) {
        if (storyRepository.existsById(id)) {
            storyRepository.deleteById(id);
            return Optional.empty();
        }
        throw new ResourceNotFoundException("Story: " + id + " not found.");
    }
}
