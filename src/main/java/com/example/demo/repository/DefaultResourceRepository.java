package com.example.demo.repository;

import com.example.demo.domain.Delta;
import com.example.demo.domain.Resource;
import com.example.demo.domain.ResourceListSnapshot;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Repository
public class DefaultResourceRepository implements ResourceRepository {

    private ResourceListSnapshot initialState;
    private ResourceListSnapshot recentState;

    private final Clock clock;
    private final List<Delta> deltas = new LinkedList<>();



    public DefaultResourceRepository() {
        this(Clock.systemUTC());
    }


    public DefaultResourceRepository(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }


    @Override
    public void registerTimeSnapshot(ResourceListSnapshot snapshot) {
        if(initialState==null){
            initialState = recentState = snapshot;
        }else {

            Set<Resource> removed = Sets.difference(recentState.getResources(), snapshot.getResources());
            Set<Resource> added = Sets.difference(snapshot.getResources(), recentState.getResources());

            if(!removed.isEmpty() || !added.isEmpty()){
                Delta delta = Delta.of(snapshot.getInstant(), added, removed);
                deltas.add(delta);

                recentState = snapshot;
            }
        }
    }

    @Override
    public List<Delta> getDeltas(Duration duration) {
        Instant initialInstant = Instant.now(clock).minus(duration);

        List<Delta> deltasInRage = new ArrayList<>();

        for (int i = deltas.size() - 1; i >= 0; i--) {
            Instant timestamp = deltas.get(i).timestamp;
            if(timestamp.isBefore(initialInstant)){
                break;
            }
            deltasInRage.add(deltas.get(i));
        }

        if(initialState!=null && initialState.getInstant().isAfter(initialInstant)){
            deltasInRage.add(Delta.of(initialState.getInstant(),initialState.getResources(),null));
        }

        return deltasInRage;
    }


}
