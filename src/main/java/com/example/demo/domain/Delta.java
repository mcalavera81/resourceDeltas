package com.example.demo.domain;

import lombok.Value;

import java.time.Instant;
import java.util.Set;

@Value(staticConstructor = "of")
public class Delta {

    public final Instant timestamp;
    public final Set<Resource> added;
    public final Set<Resource> removed;
}
