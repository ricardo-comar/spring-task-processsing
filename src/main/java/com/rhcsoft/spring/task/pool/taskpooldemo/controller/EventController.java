package com.rhcsoft.spring.task.pool.taskpooldemo.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventDoc;
import com.rhcsoft.spring.task.pool.taskpooldemo.model.EventNotification;
import com.rhcsoft.spring.task.pool.taskpooldemo.service.EventDocProcessor;
import com.rhcsoft.spring.task.pool.taskpooldemo.service.EventDocService;

@RestController
@RequestMapping("/api")
public class EventController {

    private static final Logger LOGGER = Logger.getLogger(EventController.class.getName());

    @Autowired
    private EventDocService eventDocService;

    @Autowired
    private EventDocProcessor eventDocProcessor;

    @PostMapping("/data")
    public ResponseEntity<EventDoc> postData(@RequestBody EventNotification data) {

        LOGGER.info("Event received: " + data.getEventId());

        return eventDocService.saveEvent(data).map(eventDoc -> {

            LOGGER.info("Event saved for async processing: " + data.getEventId());
            eventDocProcessor.processEvent(data.getEventId());
            return ResponseEntity.status(HttpStatus.CREATED).body(eventDoc);
        })
                .orElseGet(() -> {
                    LOGGER.severe("Event could not be saved: " + data.getEventId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                });
    }

    @GetMapping("/data/user/{id}")
    public ResponseEntity<EventDoc> getData(@PathVariable String id) {

        LOGGER.info("Event requested: " + id);
        return eventDocService.getEvent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}