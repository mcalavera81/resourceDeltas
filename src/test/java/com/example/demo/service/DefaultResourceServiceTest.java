package com.example.demo.service;

import com.example.demo.domain.AggregatedDelta;
import com.example.demo.domain.Resource;
import com.example.demo.domain.ResourceListSnapshot;
import com.example.demo.repository.DefaultResourceRepository;
import com.example.demo.repository.ResourceRepository;
import com.example.demo.utils.ResourceTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DefaultResourceServiceTest {


    private Instant present;
    private ResourceRepository repo;
    private ResourceService service;

    @Before
    public void setUp() {
        present = Instant.parse("2010-01-22T10:15:30Z");
        repo = new DefaultResourceRepository(Clock.fixed(present, ZoneId.of("UTC")));
        service = new DefaultResourceService(repo);
    }

    @Test
    public void test_NoDeltasStored_NoDataInQueryRange() {

        repo.registerTimeSnapshot(snapshotInitialState(present.minusSeconds(60)));

        assertRecentChanges(Duration.ofSeconds(30), List.of(), List.of());


    }

    @Test
    public void test_NoDeltasStored_InitialSnapshotInQueryRange() {

        repo.registerTimeSnapshot(snapshotInitialState(present.minusSeconds(60)));

        assertRecentChanges(Duration.ofSeconds(61), List.of("id_1","id_2","id_3"), List.of());

    }

    @Test
    public void test_1DeltaStored_1DeltaInQueryRange() {

        repo.registerTimeSnapshot(snapshotInitialState(present.minusSeconds(60)));

        repo.registerTimeSnapshot(snapshot(present.minusSeconds(30), List.of("id_1", "id_2", "id_4")));

        assertRecentChanges(Duration.ofSeconds(35), List.of("id_4"), List.of("id_3"));

    }


    @Test
    public void test_EmptyDelta() {
        repo.registerTimeSnapshot(snapshotInitialState(present.minusSeconds(60)));

        repo.registerTimeSnapshot(snapshot(present.minusSeconds(30), List.of("id_1", "id_2", "id_3")));

        assertRecentChanges(Duration.ofSeconds(35), List.of(), List.of());

    }

    @Test
    public void test_3DeltaStored() {
        repo.registerTimeSnapshot(snapshotInitialState(present.minusSeconds(60)));

        repo.registerTimeSnapshot(snapshot(present.minusSeconds(30), List.of("id_1", "id_2", "id_4")));
        repo.registerTimeSnapshot(snapshot(present.minusSeconds(15), List.of("id_2", "id_4", "id_5", "id_6")));
        repo.registerTimeSnapshot(snapshot(present.minusSeconds(12), List.of("id_2", "id_4", "id_5", "id_6")));
        repo.registerTimeSnapshot(snapshot(present.minusSeconds(10), List.of("id_2", "id_4", "id_5")));


        assertRecentChanges(Duration.ofSeconds(5), List.of(), List.of());

        assertRecentChanges(Duration.ofSeconds(20), List.of("id_5"), List.of("id_1"));

        assertRecentChanges(Duration.ofSeconds(35), List.of("id_4","id_5"), List.of("id_1","id_3"));

        assertRecentChanges(Duration.ofSeconds(65), List.of("id_2", "id_4", "id_5"), List.of());

    }

    private void assertRecentChanges(Duration changeSpan, List<String> added, List<String> removed) {

        AggregatedDelta recentChanges = service.recentChanges(changeSpan);

        assertThat(recentChanges)
                .extracting("duration", "added", "removed")
                .containsExactly(changeSpan, ResourceTestUtils.resource(added), ResourceTestUtils.resource(removed));
    }

    private ResourceListSnapshot snapshot(Instant instant, List<String> ids) {
        Resource[] resources = ResourceTestUtils.resource(ids).toArray(Resource[]::new);
        return ResourceListSnapshot.of(instant, resources);
    }

    private ResourceListSnapshot snapshotInitialState(Instant instant) {
        return ResourceListSnapshot.of(instant, ResourceTestUtils.resource(List.of("id_1","id_2","id_3")).toArray(Resource[]::new));
    }

}