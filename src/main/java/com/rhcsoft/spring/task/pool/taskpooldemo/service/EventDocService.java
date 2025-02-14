package com.rhcsoft.spring.task.pool.taskpooldemo.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventDoc;
import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventState;
import com.rhcsoft.spring.task.pool.taskpooldemo.model.EventNotification;
import com.rhcsoft.spring.task.pool.taskpooldemo.repo.EventDocRepository;

@Service
public class EventDocService {

    @Autowired
    private EventDocRepository eventDocRepository;

    public Optional<EventDoc> saveEvent(EventNotification eventNotification) {

        EventDoc eventDoc = new EventDoc();
        eventDoc.setId(eventNotification.getEventId());
        eventDoc.setEventDateTime(OffsetDateTime.now());
        eventDoc.setUserId(eventNotification.getUser().getId());
        eventDoc.setField1(eventNotification.getData().getField1());
        eventDoc.setField2(eventNotification.getData().getField2());
        eventDoc.setState(EventState.NEW);

        // Save the eventNotification to the database
        return Optional.of(eventDocRepository.save(eventDoc));
    }

    public void updateEventState(String eventId, EventState state) {

        EventDoc eventDoc = eventDocRepository.findById(eventId).get();
        eventDoc.setState(state);

        // Update the eventNotification in the database
        eventDocRepository.save(eventDoc);
    }

    public Optional<EventDoc> getEvent(String eventId) {
        return eventDocRepository.findById(eventId);
    }
}
