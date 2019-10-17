package com.example.demo.api;

import com.example.demo.domain.AggregatedDelta;
import com.example.demo.domain.Delta;
import com.example.demo.domain.Resource;
import com.example.demo.service.ResourceService;
import com.example.demo.utils.ResourceTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ResourceController.class)
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService service;

    @Test
    public void noChangesShouldReturnEmptySets() throws Exception {

        Duration duration = Duration.ofSeconds(30);
        when(service.recentChanges(duration)).thenReturn(AggregatedDelta.of(duration, List.of()));

        this.mockMvc.perform(get("/resource")).andExpect(status().isOk()).
                andExpect(jsonPath("$.duration", is(duration.toString()))).
                andExpect(jsonPath("$.added", hasSize(0))).
                andExpect(jsonPath("$.removed", hasSize(0)));

     }

    @Test
    public void changesAvailableShouldReturnNonEmptySets() throws Exception {


        Duration duration = Duration.ofSeconds(30);
        Set<Resource> added = ResourceTestUtils.resource(List.of("added_1", "added_2"));
        Set<Resource> removed = ResourceTestUtils.resource(List.of("removed_3"));

        AggregatedDelta aggDelta = AggregatedDelta.of(duration, List.of(Delta.of(Instant.now(), added, removed)));

        when(service.recentChanges(duration)).thenReturn(aggDelta);

        this.mockMvc.perform(get("/resource")).andExpect(status().isOk()).
                andExpect(jsonPath("$.duration", is(duration.toString()))).
                andExpect(jsonPath("$.added[*].id").value(containsInAnyOrder("added_1","added_2"))).
                andExpect(jsonPath("$.removed[*].id", containsInAnyOrder("removed_3")));

    }
}