package com.example.demo.service;

import com.example.demo.domain.AggregatedDelta;
import com.example.demo.domain.Delta;
import com.example.demo.domain.ResourceListSnapshot;
import com.example.demo.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class DefaultResourceService implements  ResourceService {

    private final ResourceRepository repository;

    public DefaultResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public AggregatedDelta recentChanges(Duration duration) {

        List<Delta> deltasInRage = repository.getDeltas(duration);

        return AggregatedDelta.of(duration, deltasInRage);
    }

    @Override
    public void registerTimeSnapshot(ResourceListSnapshot snapshot) {
        repository.registerTimeSnapshot(snapshot);
    }
}
