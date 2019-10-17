
package com.example.demo.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    private String id;
    private String model;
    private String licencePlate;
    private String resourceType;

}
