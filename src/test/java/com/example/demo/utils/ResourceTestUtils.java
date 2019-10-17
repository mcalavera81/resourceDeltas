package com.example.demo.utils;

import com.example.demo.domain.Resource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceTestUtils {


    public static Set<Resource> resource(List<String> ids) {
        return ids.stream().map(id -> new Resource(id, "model","licensePlate","CAR")).collect(Collectors.toSet());
    }
}
