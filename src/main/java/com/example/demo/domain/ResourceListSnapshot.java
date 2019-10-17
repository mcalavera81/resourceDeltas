package com.example.demo.domain;

import com.google.common.collect.Sets;
import lombok.Value;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Value
public class ResourceListSnapshot {

    private final Instant instant;

    private final Set<Resource> resources;

    private ResourceListSnapshot(Instant instant, Resource[] resources) {
        this.resources =  Sets.newHashSet(Objects.requireNonNull(resources));
        this.instant = Objects.requireNonNull(instant);
    }

    public static ResourceListSnapshot of(Instant instant, Resource[] resources) {
        return  new ResourceListSnapshot(instant, resources);
    }

}
