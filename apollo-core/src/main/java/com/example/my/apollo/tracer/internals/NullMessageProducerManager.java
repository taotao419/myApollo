package com.example.my.apollo.tracer.internals;

import com.example.my.apollo.tracer.spi.MessageProducer;
import com.example.my.apollo.tracer.spi.MessageProducerManager;

/**
 * NullMessageProducerManager
 */
public class NullMessageProducerManager implements MessageProducerManager {
    private static final MessageProducer producer = new NullMessageProducer();

    @Override
    public MessageProducer getProducer() {
        return producer;
    }

    
}