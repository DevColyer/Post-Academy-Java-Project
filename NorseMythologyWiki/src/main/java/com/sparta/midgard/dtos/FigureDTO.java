package com.sparta.midgard.dtos;

import com.sparta.midgard.models.Figure;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "figures")
public class FigureDTO extends RepresentationModel<FigureDTO> {
    private int id;
    private String name;
    private String imageLink;

    public FigureDTO(Figure figure) {
        this.id = figure.getId();
        this.name = figure.getName();
        this.imageLink = figure.getImageLink();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return(o instanceof FigureDTO figureDTO)
                && id == figureDTO.id
                && name.equals(figureDTO.name)
                && imageLink.equals(figureDTO.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageLink);
    }
}
