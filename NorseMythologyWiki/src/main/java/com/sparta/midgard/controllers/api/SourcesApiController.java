package com.sparta.midgard.controllers.api;

import com.sparta.midgard.models.Figure;
import com.sparta.midgard.models.StorySource;
import com.sparta.midgard.services.SourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sources")
public class SourcesApiController {
    private final SourcesService sourcesService;

    @Autowired
    public SourcesApiController(SourcesService sourcesService) {
        this.sourcesService = sourcesService;
    }

    @PostMapping("/create")
    public ResponseEntity<StorySource> createSource(@RequestBody StorySource source) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            Optional<StorySource> result = sourcesService.createStorySource(source);

            return result
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<StorySource>> getSources() {
        return ResponseEntity.ok().body(sourcesService.getAllStorySources());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorySource> getSource(@PathVariable int id) {
        Optional<StorySource> result = sourcesService.getStorySourceById(id);

        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StorySource> updateSource(@PathVariable int id, @RequestBody StorySource source) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            Optional<StorySource> updateSource = sourcesService.updateStorySource(id, source.getName(), source.getDiscoveryLocation(), source.getDiscoveryDate());
            return updateSource
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StorySource> deleteFigure(@PathVariable final int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            Optional<StorySource> source = sourcesService.deleteStorySourceById(id);
            if (source.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
