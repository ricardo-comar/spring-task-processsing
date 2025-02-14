package com.rhcsoft.spring.task.pool.taskpooldemo.model;

import lombok.Data;

@Data
public class EventNotification {

    private String eventId;

    private EventUser user;

    private EventData data;

}
