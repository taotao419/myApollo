package com.example.my.apollo.tracer.internals;

import com.example.my.apollo.tracer.spi.MessageProducer;
import com.example.my.apollo.tracer.spi.Transaction;

/**
 * NullMessageProducer
 */
public class NullMessageProducer implements MessageProducer {
    private static final Transaction NULL_TRANSACTION=new NullTransaction();

    @Override
    public void logError(Throwable cause) {
    }

    @Override
    public void logError(String message, Throwable cause) {
    }

    @Override
    public void logEvent(String type, String name) {
    }

    @Override
    public void logEvent(String type, String name, String status, String nameValuePairs) {
    }

    @Override
    public Transaction newTransaction(String type, String name) {
        return NULL_TRANSACTION;
    }
}