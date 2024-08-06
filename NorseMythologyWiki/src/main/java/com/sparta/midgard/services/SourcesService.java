package com.sparta.midgard.services;

import com.sparta.midgard.exceptions.ResourceNotFoundException;
import com.sparta.midgard.models.Story;
import com.sparta.midgard.models.StorySource;
import com.sparta.midgard.repositories.StorySourceRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SourcesService {
    private final StorySourceRepository storySourceRepository;

    @Autowired
    public SourcesService(StorySourceRepository storySourceRepository) {
        this.storySourceRepository = storySourceRepository;
    }

    public Optional<StorySource> createStorySource(StorySource storySource) {
        if (storySourceRepository.existsById(storySource.getId())) {
            return Optional.empty();
        }

        return Optional.of(storySourceRepository.save(storySource));
    }

    public List<StorySource> getAllStorySources() {
        return storySourceRepository.findAll();
    }

    public Optional<StorySource> getStorySourceById(int id) {
        return storySourceRepository.findById(id);
    }

    public Optional<StorySource> updateStorySource(int id, String name, String discoveryLocation, LocalDate discoveryDate) {
        if (storySourceRepository.existsById(id)) {
            StorySource storySource = storySourceRepository.findById(id).get();
            if(name != null && !name.isEmpty()) {
                storySource.setName(name);
            }
            if(discoveryLocation != null && !discoveryLocation.isEmpty()) {
                storySource.setDiscoveryLocation(discoveryLocation);
            }
            if(discoveryDate != null && discoveryDate.isBefore(LocalDate.now())) {
                storySource.setDiscoveryDate(discoveryDate);
            }
            return Optional.of(storySourceRepository.save(storySource));
        }
        throw new ResourceNotFoundException("Story Source with id " + id + " not found");
    }

    public Optional<StorySource> deleteStorySourceById(int id) {
        if (storySourceRepository.existsById(id)) {
            storySourceRepository.deleteById(id);
            return Optional.empty();
        }
        throw new ResourceNotFoundException("Story Source with id " + id + " not found");
    }
}
