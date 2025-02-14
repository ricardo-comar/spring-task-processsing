package com.rhcsoft.spring.task.pool.taskpooldemo.document;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class EventDoc {

    @Id
    private String id;

    private String userId;

    private String field1;

    private String field2;

    private EventState state;

    private OffsetDateTime eventDateTime;

}
