package com.sparta.midgard.controllers.api;

import com.sparta.midgard.dtos.FigureDTO;
import com.sparta.midgard.models.Figure;
import com.sparta.midgard.services.FiguresService;
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
@RequestMapping("/figures")
public class FiguresApiController {
    private final FiguresService figuresService;

    @Autowired
    public FiguresApiController(final FiguresService figuresService) {
        this.figuresService = figuresService;
    }

    @PostMapping("/create")
    public ResponseEntity<Figure> createFigure(@RequestBody final Figure figure) {
        boolean roleAdmin = StaticUtils.isRoleAdmin();

        if (roleAdmin) {
            Optional<Figure> result = figuresService.createFigure(figure);

            return result
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Figure>> getFigures() {
        return ResponseEntity.ok(figuresService.getFigures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Figure> getFigureById(@PathVariable final int id) {
        Optional<Figure> result = figuresService.getFigureById(id);

        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search/name/{name}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FigureDTO>> searchFiguresByName(@PathVariable String name) {
        return ResponseEntity.ok(figuresService.searchFiguresByName(name).
                map(FigureDTO::new)
                .toList());
    }

    @GetMapping("/search/story/{storyName}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FigureDTO>> searchFiguresByStory(@PathVariable String storyName) {
        return ResponseEntity.ok(figuresService.getFiguresByStory(storyName).
                map(FigureDTO::new)
                .toList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Figure> updateFigure(@PathVariable final int id, @RequestBody final Figure figure) {
        boolean roleAdmin = StaticUtils.isRoleAdmin();

        if (roleAdmin) {
            Optional<Figure> updatedFigure = figuresService.updateFigure(id, figure.getName(), figure.getImageLink());
            return updatedFigure
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Figure> deleteFigure(@PathVariable final int id) {
        boolean roleAdmin = StaticUtils.isRoleAdmin();

        if (roleAdmin) {
            Optional<Figure> figure = figuresService.deleteFigure(id);
            if (figure.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
