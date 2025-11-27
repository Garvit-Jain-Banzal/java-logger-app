package com.example.logger;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Simple Java application that emits log statements forever and exposes a health endpoint.
 */
public final class LogStreamingApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LogStreamingApplication.class);
    private static final int DEFAULT_DELAY_MILLIS = 1_000;
    private static final int SERVER_PORT = 8080;

    private LogStreamingApplication() {
        // Utility class
    }

    public static void main(String[] args) throws InterruptedException {
        startHealthEndpoint();

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

    private static void startHealthEndpoint() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
            server.createContext("/health", exchange -> {
                byte[] body = "OK".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
            });
            server.createContext("/", exchange -> {
                byte[] body = "Continuous Logger is running".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
            });
            server.setExecutor(Executors.newSingleThreadExecutor());
            server.start();
            LOG.info("Health endpoint started on port {}.", SERVER_PORT);
        } catch (IOException e) {
            LOG.error("Failed to start health endpoint", e);
        }
    }
}

