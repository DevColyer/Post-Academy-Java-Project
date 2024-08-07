package com.sparta.midgard.dtos;

import com.sparta.midgard.models.Story;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "stories")
public class StoryDTO extends RepresentationModel<StoryDTO> {
    private Integer id;
    private String name;
    private int sourceId;

    public StoryDTO(Story story) {
        this.id = story.getId();
        this.name = story.getName();
        this.sourceId = story.getSource().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        return (o instanceof StoryDTO storyDTO)
                && sourceId == storyDTO.sourceId
                && id.equals(storyDTO.id)
                && name.equals(storyDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sourceId);
    }
}
