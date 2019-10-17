package com.example.demo.repository;

import com.example.demo.domain.Delta;
import com.example.demo.domain.ResourceListSnapshot;

import java.time.Duration;
import java.util.List;

public interface ResourceRepository {

    void registerTimeSnapshot(ResourceListSnapshot snapshot);

    List<Delta> getDeltas(Duration duration);
}
