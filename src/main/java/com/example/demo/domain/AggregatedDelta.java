package com.example.demo.domain;

import com.google.common.collect.Sets;
import lombok.Value;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Value
public class AggregatedDelta {

    private final Duration duration;
    private final Set<Resource> added;
    private final Set<Resource> removed;

    private AggregatedDelta(Duration duration){
        this.duration = duration;
        this.added = new HashSet<>();
        this.removed = new HashSet<>();

    }

    public static AggregatedDelta of(Duration duration, List<Delta> deltas){

        AggregatedDelta aggregatedDelta = deltas.stream().collect(
                () -> new AggregatedDelta(duration), AggregatedDelta::accumulate, AggregatedDelta::combine);


        Sets.intersection(aggregatedDelta.getAdded(), aggregatedDelta.getRemoved()).immutableCopy().forEach(elem->{
            aggregatedDelta.getAdded().remove(elem);
            aggregatedDelta.getRemoved().remove(elem);
        });

        return aggregatedDelta;
    }


    private void accumulate(Delta delta) {
        if (delta.added!=null) {
            this.added.addAll(delta.added);
        }

        if (delta.removed!=null) {
            this.removed.addAll(delta.removed);
        }
    }

    private void combine(AggregatedDelta aggregatedDelta) {
        this.added.addAll(aggregatedDelta.added);
        this.removed.addAll(aggregatedDelta.removed);
    }

}
