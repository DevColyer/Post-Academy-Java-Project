package com.sparta.midgard.controllers.api;

import com.sparta.midgard.dtos.StoryDTO;
import com.sparta.midgard.models.Story;
import com.sparta.midgard.services.StoriesService;
import com.sparta.midgard.utils.StaticUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stories")
public class StoriesApiController {
    private final StoriesService storiesService;

    @Autowired
    public StoriesApiController(StoriesService storiesService) {
        this.storiesService = storiesService;
    }

    @PostMapping("/create")
    public ResponseEntity<StoryDTO> createStory(@RequestBody Story story, HttpServletRequest request) {
        if (StaticUtils.isRoleAdmin()) {
            return storiesService.createStory(story)
                    .map(createdStory -> createStoryResponse(createdStory, request))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<StoryDTO>> getStories(HttpServletRequest request) {
        Set<StoryDTO> storyDTOs = storiesService.getStories().stream()
                .map(StoryDTO::new)
                .peek(storyDTO -> addHateoasLinks(storyDTO, request))
                .collect(Collectors.toSet());

        CollectionModel<StoryDTO> collectionModel = CollectionModel.of(storyDTOs);
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDTO> getStory(@PathVariable int id, HttpServletRequest request) {
        StoryDTO storyDTO = storiesService.getStoryById(id)
                .map(StoryDTO::new)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No story with id: " + id + " exists."
                ));

        addHateoasLinks(storyDTO, request);
        return ResponseEntity.ok(storyDTO);
    }

    @GetMapping("/search/name/{storyName}")
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<StoryDTO>> searchStoriesByName(@PathVariable String storyName, HttpServletRequest request) {
        Set<StoryDTO> storyDTOs = storiesService.searchStoryByName(storyName)
                .map(StoryDTO::new)
                .peek(storyDTO -> addHateoasLinks(storyDTO, request))
                .collect(Collectors.toSet());

        CollectionModel<StoryDTO> collectionModel = CollectionModel.of(storyDTOs);
        collectionModel.add(Link.of(StaticUtils.getRequestBaseUrl(request) + "/api/stories").withRel("All Stories"));
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/search/figures/{figureName}")
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<StoryDTO>> searchStoriesByFigure(@PathVariable String figureName, HttpServletRequest request) {
        Set<StoryDTO> storyDTOs = storiesService.searchStoriesByFigure(figureName)
                .map(StoryDTO::new)
                .peek(storyDTO -> addHateoasLinks(storyDTO, request))
                .collect(Collectors.toSet());

        CollectionModel<StoryDTO> collectionModel = CollectionModel.of(storyDTOs);
        collectionModel.add(Link.of(StaticUtils.getRequestBaseUrl(request) + "/api/stories").withRel("All Stories"));
        return ResponseEntity.ok(collectionModel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StoryDTO> updateStory(@PathVariable int id, @RequestBody Story story, HttpServletRequest request) {
        if (StaticUtils.isRoleAdmin()) {
            return storiesService.updateStory(id, story.getName(), story.getSource().getId())
                    .map(updatedStory -> createStoryResponse(updatedStory, request))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable int id) {
        if (StaticUtils.isRoleAdmin()) {
            Optional<Story> story = storiesService.deleteStory(id);
            if (story.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private ResponseEntity<StoryDTO> createStoryResponse(Story story, HttpServletRequest request) {
        String baseUrl = StaticUtils.getRequestBaseUrl(request);
        StoryDTO storyDTO = new StoryDTO(story);
        StaticUtils.addHateoasLink(storyDTO, baseUrl, "/api/stories/" + storyDTO.getId(), storyDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(storyDTO);
    }

    private void addHateoasLinks(StoryDTO storyDTO, HttpServletRequest request) {
        String baseUrl = StaticUtils.getRequestBaseUrl(request);
        StaticUtils.addHateoasLink(storyDTO, baseUrl, "/api/stories/" + storyDTO.getId(), storyDTO.getName());
        StaticUtils.addHateoasLink(storyDTO, baseUrl, "/api/figures/search/story/" + storyDTO.getName().replace(" ", "%20"), "Figures");
    }
}
