package com.biblioteca.prestamo.util;

import com.biblioteca.common.dto.PrestamoEvent;
import com.biblioteca.prestamo.messaging.PrestamoEventPublisher;

/**
 * Test implementation of PrestamoEventPublisher that performs no-op operations.
 * Used in tests to avoid mocking RabbitTemplate and Byte Buddy instrumentation issues.
 */
public class TestPrestamoEventPublisher extends PrestamoEventPublisher {

    public TestPrestamoEventPublisher() {
        super(null);
    }

    @Override
    public void publishPrestamoEvent(PrestamoEvent event) {
        // no-op for tests - no RabbitTemplate interaction
    }
}

