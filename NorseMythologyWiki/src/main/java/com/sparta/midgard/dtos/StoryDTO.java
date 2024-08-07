package com.sparta.midgard.dtos;

import com.sparta.midgard.models.Story;
import com.sparta.midgard.models.StorySource;

public class StoryDTO {
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

}
