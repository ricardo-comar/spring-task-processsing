package com.rhcsoft.spring.task.pool.taskpooldemo.controller;

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

    @Autowired
    private EventDocService eventDocService;

    @Autowired
    private EventDocProcessor eventDocProcessor;

    @PostMapping("/data")
    public ResponseEntity<EventDoc> postData(@RequestBody EventNotification data) {
        return eventDocService.saveEvent(data).map(eventDoc -> {
            eventDocProcessor.processEvent(data.getEventId());
            return ResponseEntity.status(HttpStatus.CREATED).body(eventDoc);
        })
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/data/user/{id}")
    public ResponseEntity<EventDoc> getData(@PathVariable String id) {
        return eventDocService.getEvent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}