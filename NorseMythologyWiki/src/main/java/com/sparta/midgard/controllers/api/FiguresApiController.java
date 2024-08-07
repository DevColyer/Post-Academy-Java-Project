package com.sparta.midgard.controllers.api;

import com.sparta.midgard.dtos.FigureDTO;
import com.sparta.midgard.models.Figure;
import com.sparta.midgard.services.FiguresService;
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
@RequestMapping("/api/figures")
public class FiguresApiController {

    private final FiguresService figuresService;

    @Autowired
    public FiguresApiController(FiguresService figuresService) {
        this.figuresService = figuresService;
    }

    @PostMapping("/create")
    public ResponseEntity<FigureDTO> createFigure(@RequestBody Figure figure, HttpServletRequest request) {
        if (StaticUtils.isRoleAdmin()) {
            return figuresService.createFigure(figure)
                    .map(newFigure -> createFigureResponse(newFigure, request))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<FigureDTO>> getFigures(HttpServletRequest request) {
        Set<FigureDTO> figureDTOs = figuresService.getFigures().stream()
                .map(FigureDTO::new)
                .peek(figure -> addHateoasLinks(figure, request))
                .collect(Collectors.toSet());

        CollectionModel<FigureDTO> collectionModel = CollectionModel.of(figureDTOs);
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FigureDTO> getFigureById(@PathVariable int id, HttpServletRequest request) {
        FigureDTO figureDTO = figuresService.getFigureById(id)
                .map(FigureDTO::new)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No figure with id: " + id + " exists."
                ));

        addHateoasLinks(figureDTO, request);
        return ResponseEntity.ok(figureDTO);
    }

    @GetMapping("/search/name/{name}")
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<FigureDTO>> searchFiguresByName(@PathVariable String name, HttpServletRequest request) {
        Set<FigureDTO> figureDTOs = figuresService.searchFiguresByName(name)
                .map(FigureDTO::new)
                .peek(figure -> addHateoasLinks(figure, request))
                .collect(Collectors.toSet());

        CollectionModel<FigureDTO> collectionModel = CollectionModel.of(figureDTOs);
        collectionModel.add(Link.of(StaticUtils.getRequestBaseUrl(request) + "/api/figures").withRel("All Figures"));
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/search/story/{storyName}")
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<FigureDTO>> searchFiguresByStory(@PathVariable String storyName, HttpServletRequest request) {
        Set<FigureDTO> figureDTOs = figuresService.getFiguresByStory(storyName)
                .map(FigureDTO::new)
                .peek(figure -> addHateoasLinks(figure, request))
                .collect(Collectors.toSet());

        CollectionModel<FigureDTO> collectionModel = CollectionModel.of(figureDTOs);
        collectionModel.add(Link.of(StaticUtils.getRequestBaseUrl(request) + "/api/figures").withRel("All Figures"));
        collectionModel.add(Link.of(StaticUtils.getRequestBaseUrl(request) + "/api/stories").withRel("All Stories"));
        return ResponseEntity.ok(collectionModel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FigureDTO> updateFigure(@PathVariable int id, @RequestBody Figure figure, HttpServletRequest request) {
        if (StaticUtils.isRoleAdmin()) {
            return figuresService.updateFigure(id, figure.getName(), figure.getImageLink())
                    .map(updatedFigure -> createFigureResponse(updatedFigure, request))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFigure(@PathVariable int id) {
        if (StaticUtils.isRoleAdmin()) {
            Optional<Figure> figure = figuresService.deleteFigure(id);
            if (figure.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private ResponseEntity<FigureDTO> createFigureResponse(Figure figure, HttpServletRequest request) {
        String baseUrl = StaticUtils.getRequestBaseUrl(request);
        FigureDTO figureDTO = new FigureDTO(figure);
        StaticUtils.addHateoasLink(figureDTO, baseUrl, "/api/figures/" + figureDTO.getId(), figureDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(figureDTO);
    }

    private void addHateoasLinks(FigureDTO figureDTO, HttpServletRequest request) {
        String baseUrl = StaticUtils.getRequestBaseUrl(request);
        StaticUtils.addHateoasLink(figureDTO, baseUrl, "/api/figures/" + figureDTO.getId(), figureDTO.getName());
        StaticUtils.addHateoasLink(figureDTO, baseUrl, "/api/stories/search/figures/" + figureDTO.getName().replace(" ", "%20"), "Stories");
    }
}
