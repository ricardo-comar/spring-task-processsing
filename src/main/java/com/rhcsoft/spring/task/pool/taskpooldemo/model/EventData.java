package com.rhcsoft.spring.task.pool.taskpooldemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EventData {

    @NotBlank
    @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}[}]?$", message = "Invalid UUID format")
    private String id;

    @NotBlank
    private String field1;

    private String field2;

}
