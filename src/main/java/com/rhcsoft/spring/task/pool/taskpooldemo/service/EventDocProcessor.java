package com.rhcsoft.spring.task.pool.taskpooldemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventState;

@Service
public class EventDocProcessor {

    @Autowired
    private EventDocService service;

    @Async
    public void processEvent(String eventId) {

        service.updateEventState(eventId, EventState.IN_PROGRESS);

        service.getEvent(eventId).ifPresentOrElse(eventDoc -> {
            try {
                Thread.sleep(5000); // Simula um delay de 5 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Processamento interrompido");
            }
            service.updateEventState(eventId, EventState.COMPLETED);
        }, () -> {
            service.updateEventState(eventId, EventState.FAILED);
        });
    }
}
