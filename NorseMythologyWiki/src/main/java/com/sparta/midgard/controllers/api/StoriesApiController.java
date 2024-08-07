package com.sparta.midgard.controllers.api;

import com.sparta.midgard.dtos.StoryDTO;
import com.sparta.midgard.models.Figure;
import com.sparta.midgard.models.Story;
import com.sparta.midgard.services.StoriesService;
import com.sparta.midgard.utils.StaticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stories")
public class StoriesApiController {
    private final StoriesService storiesService;

    @Autowired
    public StoriesApiController(StoriesService storiesService) {
        this.storiesService = storiesService;
    }

    @PostMapping("/create")
    public ResponseEntity<Story> createStory(@RequestBody Story story) {
        boolean roleAdmin = StaticUtils.isRoleAdmin();

        if (roleAdmin) {
            Optional<Story> result = storiesService.createStory(story);

            return result
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Story>> getStories() {
        return ResponseEntity.ok(storiesService.getStories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Story> getStory(@PathVariable int id) {
        Optional<Story> result = storiesService.getStoryById(id);

        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/name/{storyName}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<StoryDTO>> searchStoriesByName(@PathVariable String storyName) {
        return ResponseEntity.ok(storiesService.searchStoryByName(storyName)
                .map(StoryDTO::new)
                .toList());
    }

    @GetMapping("/search/figures/{figureName}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<StoryDTO>> searchStoriesByFigure(@PathVariable String figureName) {
        return ResponseEntity.ok(storiesService.searchStoriesByFigure(figureName)
                .map(StoryDTO::new)
                .toList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Story> updateStory(@PathVariable int id, @RequestBody Story story) {
        boolean roleAdmin = StaticUtils.isRoleAdmin();
        if (roleAdmin) {
            Optional<Story> updateStory = storiesService.updateStory(id, story.getName(),story.getSource().getId());
            return updateStory
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Story> deleteStory(@PathVariable int id) {
        boolean roleAdmin = StaticUtils.isRoleAdmin();

        if (roleAdmin) {
            Optional<Story> story = storiesService.deleteStory(id);
            if (story.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
