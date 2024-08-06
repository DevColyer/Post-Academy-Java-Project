package com.sparta.midgard.dtos;

import com.sparta.midgard.models.Figure;

public class FigureDTO {
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
}
