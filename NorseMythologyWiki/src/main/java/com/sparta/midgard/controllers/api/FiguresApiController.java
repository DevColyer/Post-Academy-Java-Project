package com.sparta.midgard.controllers.api;

import com.sparta.midgard.dtos.FigureDTO;
import com.sparta.midgard.models.Figure;
import com.sparta.midgard.services.FiguresService;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
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

    @GetMapping("/search/{storyName}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FigureDTO>> searchFigures(@PathVariable String storyName) {
        return ResponseEntity.ok(figuresService.getFiguresByStory(storyName).
                map(FigureDTO::new)
                .toList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Figure> updateFigure(@PathVariable final int id, @RequestBody final Figure figure) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
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
