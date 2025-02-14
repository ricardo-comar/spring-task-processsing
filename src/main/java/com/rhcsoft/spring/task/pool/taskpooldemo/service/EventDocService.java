package com.rhcsoft.spring.task.pool.taskpooldemo.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;

import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventDoc;
import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventState;
import com.rhcsoft.spring.task.pool.taskpooldemo.model.EventNotification;
import com.rhcsoft.spring.task.pool.taskpooldemo.repo.EventDocRepository;

@Service
public class EventDocService {

    @Autowired
    private EventDocRepository eventDocRepository;

    @Autowired
    private CouchbaseTemplate couchbaseTemplate;

    public Optional<EventDoc> saveEvent(EventNotification eventNotification) {

        EventDoc eventDoc = new EventDoc();
        eventDoc.setId(eventNotification.getEventId());
        eventDoc.setReceivedAt(LocalDateTime.now());
        eventDoc.setUserId(eventNotification.getUser().getId());
        eventDoc.setField1(eventNotification.getData().getField1());
        eventDoc.setField2(eventNotification.getData().getField2());
        eventDoc.setState(EventState.NEW);

        // Save the eventNotification to the database
        return Optional
                .of(couchbaseTemplate.insertById(EventDoc.class).withExpiry(Duration.ofMinutes(10)).one(eventDoc));
    }

    public void startProcessEvent(String eventId) {
        EventDoc eventDoc = eventDocRepository.findById(eventId).get();
        eventDoc.setState(EventState.IN_PROGRESS);
        eventDoc.setStartedAt(LocalDateTime.now());
        couchbaseTemplate.replaceById(EventDoc.class).withExpiry(Duration.ofMinutes(5)).one(eventDoc);
    }

    public void completeProcessEvent(String eventId) {
        EventDoc eventDoc = eventDocRepository.findById(eventId).get();
        eventDoc.setState(EventState.COMPLETED);
        eventDoc.setCompletedAt(LocalDateTime.now());
        couchbaseTemplate.replaceById(EventDoc.class).withExpiry(Duration.ofSeconds(10)).one(eventDoc);
    }

    public void failProcessEvent(String eventId) {
        EventDoc eventDoc = eventDocRepository.findById(eventId).get();
        eventDoc.setState(EventState.FAILED);
        eventDoc.setCompletedAt(LocalDateTime.now());
        couchbaseTemplate.replaceById(EventDoc.class).withExpiry(Duration.ofSeconds(120)).one(eventDoc);
    }

    public Optional<EventDoc> getEvent(String eventId) {
        return eventDocRepository.findById(eventId);
    }
}
