package com.example.demo.service;

import com.example.demo.domain.AggregatedDelta;
import com.example.demo.domain.ResourceListSnapshot;

import java.time.Duration;

public interface ResourceService {
    AggregatedDelta recentChanges(Duration ofSeconds);
    void registerTimeSnapshot(ResourceListSnapshot snapshot);
}
