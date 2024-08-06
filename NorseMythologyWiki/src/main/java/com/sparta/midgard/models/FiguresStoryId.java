package com.sparta.midgard.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FiguresStoryId implements Serializable {
    private static final long serialVersionUID = 7646082765502364725L;
    @NotNull
    @Column(name = "figure_id", nullable = false)
    private Integer figureId;

    @NotNull
    @Column(name = "story_id", nullable = false)
    private Integer storyId;

    public Integer getFigureId() {
        return figureId;
    }

    public void setFigureId(Integer figureId) {
        this.figureId = figureId;
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FiguresStoryId entity = (FiguresStoryId) o;
        return Objects.equals(this.figureId, entity.figureId) &&
                Objects.equals(this.storyId, entity.storyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(figureId, storyId);
    }

}