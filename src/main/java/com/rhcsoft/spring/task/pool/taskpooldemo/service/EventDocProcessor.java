package com.rhcsoft.spring.task.pool.taskpooldemo.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EventDocProcessor {

    private static final ConcurrentHashMap<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    @Autowired
    private EventDocService service;

    @Async
    public void processEvent(String eventId) {

        Semaphore semaphore = semaphoreMap.computeIfAbsent(eventId, id -> new Semaphore(1));

        try {
            semaphore.acquire();
            service.startProcessEvent(eventId);

            service.getEvent(eventId).ifPresentOrElse(eventDoc -> {

                try {
                    Thread.sleep(5000); // Simula um delay de 50 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Processamento finalizado");
                }
                service.completeProcessEvent(eventId);

            }, () -> {

                service.failProcessEvent(eventId);
            });

        } catch (InterruptedException e) {
        } finally {
            semaphore.release();
        }

    }
}
