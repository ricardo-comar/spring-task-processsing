package com.rhcsoft.spring.task.pool.taskpooldemo.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EventDocProcessor {

    private static final Logger LOGGER = Logger.getLogger(EventDocProcessor.class.getName());

    private static final ConcurrentHashMap<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    @Autowired
    private EventDocService service;

    @Async
    public void processEvent(String eventId) {

        LOGGER.info("Event to be processed: " + eventId);
        Semaphore semaphore = semaphoreMap.computeIfAbsent(eventId, id -> new Semaphore(1));

        try {
            LOGGER.info("Acquiring lock for: " + eventId);
            semaphore.acquire();

            LOGGER.info("Processing event: " + eventId);
            service.startProcessEvent(eventId);

            service.getEvent(eventId).ifPresentOrElse(eventDoc -> {

                try {
                    Thread.sleep(5000); // Simula um delay de 50 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Processamento finalizado");
                }

                LOGGER.info("Successful processing event: " + eventId);
                service.completeProcessEvent(eventId);

            }, () -> {

                LOGGER.severe("Event not found on couchbase to be processed: " + eventId);
                service.failProcessEvent(eventId);
            });

        } catch (InterruptedException e) {
        } finally {
            semaphore.release();
        }

        LOGGER.info("Event processing finished: " + eventId);
    }
}
