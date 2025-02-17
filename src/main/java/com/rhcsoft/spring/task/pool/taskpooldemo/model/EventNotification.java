package com.rhcsoft.spring.task.pool.taskpooldemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EventNotification {

    @NotBlank
    @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}[}]?$", message = "Invalid UUID format")
    private String eventId;

    @NotNull
    private EventUser user;

    @NotNull
    private EventData data;

}
