package com.example.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Simple Java application that emits log statements forever.
 */
public final class LogStreamingApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LogStreamingApplication.class);
    private static final int DEFAULT_DELAY_MILLIS = 1_000;

    private LogStreamingApplication() {
        // Utility class
    }

    public static void main(String[] args) throws InterruptedException {
        int sleepMillis = DEFAULT_DELAY_MILLIS;
        if (args != null && args.length > 0) {
            try {
                sleepMillis = Integer.parseInt(args[0]);
                LOG.info("Using custom delay of {} ms between log entries.", sleepMillis);
            } catch (NumberFormatException ex) {
                LOG.warn("Invalid delay '{}', falling back to {} ms.", args[0], sleepMillis);
            }
        } else {
            LOG.info("Using default delay of {} ms between log entries.", sleepMillis);
        }

        LOG.info("Starting continuous logger. Press Ctrl+C to stop.");
        while (true) {
            LOG.info("Heartbeat at {}.", LocalDateTime.now());
            LOG.debug("Still alive. Next log in {} ms.", sleepMillis);
            TimeUnit.MILLISECONDS.sleep(sleepMillis);
        }
    }
}

