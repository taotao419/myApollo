package com.example.my.apollo.tracer;

import com.example.my.apollo.tracer.internals.NullMessageProducerManager;
import com.example.my.apollo.tracer.spi.MessageProducerManager;
import com.example.my.apollo.tracer.spi.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracer
 */
public abstract class Tracer {
    private static final Logger logger = LoggerFactory.getLogger(Tracer.class);
    private static final MessageProducerManager NULL_MESSAGE_PRODUCER_MANAGER = new NullMessageProducerManager();

    public static void logError(Throwable cause) {
        try {
            // getProducer().logError(cause);
            // do nothing;
        } catch (Throwable ex) {
            logger.warn("Failed to log error for cause: {}", cause, ex);
        }
    }

    public static void logEvent(String type, String name) {
        try {
            // getProducer().logEvent(type, name);
            // do nothing
        } catch (Throwable ex) {
            logger.warn("Failed to log event for type: {}, name: {}", type, name, ex);
        }
    }

    public static Transaction newTransaction(String type, String name) {
        try {
            return NULL_MESSAGE_PRODUCER_MANAGER.getProducer().newTransaction(type, name);
        } catch (Throwable ex) {
            logger.warn("Failed to create transaction for type: {}, name: {}", type, name, ex);
            return NULL_MESSAGE_PRODUCER_MANAGER.getProducer().newTransaction(type, name);
        }
    }
}