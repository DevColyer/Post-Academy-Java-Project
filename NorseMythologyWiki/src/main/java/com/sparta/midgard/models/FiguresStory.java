package com.sparta.midgard.models;

import jakarta.persistence.*;

@Entity
@Table(name = "figures_stories", schema = "norse_mythology")
public class FiguresStory {
    @EmbeddedId
    private FiguresStoryId id;

    @MapsId("figureId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "figure_id", nullable = false)
    private Figure figure;

    @MapsId("storyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    public FiguresStoryId getId() {
        return id;
    }

    public void setId(FiguresStoryId id) {
        this.id = id;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

}