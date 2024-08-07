package com.sparta.midgard.controllers.api;

import com.sparta.midgard.dtos.SourceDTO;
import com.sparta.midgard.models.StorySource;
import com.sparta.midgard.services.SourcesService;
import com.sparta.midgard.utils.StaticUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sources")
public class SourcesApiController {
    private final SourcesService sourcesService;

    @Autowired
    public SourcesApiController(SourcesService sourcesService) {
        this.sourcesService = sourcesService;
    }

    @PostMapping("/create")
    public ResponseEntity<SourceDTO> createSource(@RequestBody StorySource source, HttpServletRequest request) {
        if (StaticUtils.isRoleAdmin()) {
            return sourcesService.createStorySource(source)
                    .map(createdSource -> createSourceResponse(createdSource, request))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<SourceDTO>> getSources(HttpServletRequest request) {
        List<SourceDTO> sourceDTOs = sourcesService.getAllStorySources().stream()
                .map(SourceDTO::new)
                .peek(source -> addHateoasLinks(source, request))
                .collect(Collectors.toList());

        CollectionModel<SourceDTO> collectionModel = CollectionModel.of(sourceDTOs);
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SourceDTO> getSource(@PathVariable int id, HttpServletRequest request) {
        SourceDTO sourceDTO = sourcesService.getStorySourceById(id)
                .map(SourceDTO::new)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No source with id: " + id + " exists."
                ));

        addHateoasLinks(sourceDTO, request);
        return ResponseEntity.ok(sourceDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SourceDTO> updateSource(@PathVariable int id, @RequestBody StorySource source, HttpServletRequest request) {
        if (StaticUtils.isRoleAdmin()) {
            return sourcesService.updateStorySource(id, source.getName(), source.getDiscoveryLocation(), source.getDiscoveryDate())
                    .map(updatedSource -> createSourceResponse(updatedSource, request))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable int id) {
        if (StaticUtils.isRoleAdmin()) {
            Optional<StorySource> source = sourcesService.deleteStorySourceById(id);
            if (source.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private ResponseEntity<SourceDTO> createSourceResponse(StorySource source, HttpServletRequest request) {
        String baseUrl = StaticUtils.getRequestBaseUrl(request);
        SourceDTO sourceDTO = new SourceDTO(source);
        StaticUtils.addHateoasLink(sourceDTO, baseUrl, "/api/sources/" + sourceDTO.getId(), sourceDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(sourceDTO);
    }

    private void addHateoasLinks(SourceDTO sourceDTO, HttpServletRequest request) {
        String baseUrl = StaticUtils.getRequestBaseUrl(request);
        StaticUtils.addHateoasLink(sourceDTO, baseUrl, "/api/sources/" + sourceDTO.getId(), sourceDTO.getName());
    }
}
