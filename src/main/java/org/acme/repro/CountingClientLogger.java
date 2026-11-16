package org.acme.repro;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.inject.Singleton;

import org.jboss.resteasy.reactive.client.api.ClientLogger;
import org.jboss.resteasy.reactive.client.logging.DefaultClientLogger;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/**
 * Delegates to the real {@link DefaultClientLogger} and counts how many times
 * {@code logResponse()} is called.
 */
@Singleton
public class CountingClientLogger implements ClientLogger {
    public static final AtomicInteger COUNTER = new AtomicInteger();

    private final DefaultClientLogger delegate = new DefaultClientLogger();

    @Override
    public void setBodySize(int bodySize) {
        delegate.setBodySize(bodySize);
    }

    @Override
    public void logResponse(HttpClientResponse response, boolean redirect) {
        COUNTER.incrementAndGet();
        delegate.logResponse(response, redirect);
    }

    @Override
    public void logRequest(HttpClientRequest request, Buffer body, boolean omitBody) {
        delegate.logRequest(request, body, omitBody);
    }
}
