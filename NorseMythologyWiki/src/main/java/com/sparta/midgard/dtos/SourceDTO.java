package com.sparta.midgard.dtos;

import com.sparta.midgard.models.StorySource;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "story_source")
public class SourceDTO extends RepresentationModel<SourceDTO> {
    private Integer id;
    private String name;
    private String discoveryLocation;
    private String discoveryDate;

    public SourceDTO(StorySource source) {
        this.id = source.getId();
        this.name = source.getName();
        this.discoveryLocation = source.getDiscoveryLocation();
        this.discoveryDate = source.getDiscoveryDate().toString();
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

    public String getDiscoveryLocation() {
        return discoveryLocation;
    }

    public void setDiscoveryLocation(String discoveryLocation) {
        this.discoveryLocation = discoveryLocation;
    }

    public String getDiscoveryDate() {
        return discoveryDate;
    }

    public void setDiscoveryDate(String discoveryDate) {
        this.discoveryDate = discoveryDate;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        return (o instanceof SourceDTO sourceDTO)
                && id.equals(sourceDTO.id)
                && name.equals(sourceDTO.name)
                && discoveryLocation.equals(sourceDTO.discoveryLocation)
                && discoveryDate.equals(sourceDTO.discoveryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, discoveryLocation, discoveryDate);
    }
}
