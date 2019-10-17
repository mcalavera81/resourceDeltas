package com.example.demo.api;

import com.example.demo.domain.AggregatedDelta;
import com.example.demo.service.ResourceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.time.Duration;

@RestController
@RequestMapping(value = "resource",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ResourceController {


    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }


    @GetMapping
    public ResponseEntity<AggregatedDelta> recentChanges(
            @RequestParam(name = "span",defaultValue = "30")
            @Min(30) Integer spanSeconds) {

        return ResponseEntity.ok(resourceService.recentChanges(Duration.ofSeconds(spanSeconds)));
    }

}