package org.acme.repro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Reproduces unbounded recursion in the Quarkus RestClient when a multipart response is
 * received while client logging is enabled.
 * <p>
 * Mechanism:
 * <ul>
 * <li>{@code ClientSendRequestHandler}'s multipart branch registers an {@code endHandler}
 *     on the response and calls {@code clientLogger.logResponse(response, false)} from
 *     inside it -- after the response has already ended.</li>
 * <li>{@code DefaultClientLogger.logResponse()} calls {@code response.bodyHandler(h)}, which
 *     delegates to {@code body().onSuccess(h)}.</li>
 * <li>{@code HttpClientResponseImpl.body()} unconditionally calls {@code eventHandler.handleEnd()}
 *     whenever {@code ended == ENDED_SENTINEL}, which is always true here.</li>
 * <li>{@code HttpEventHandler.handleEnd()} dispatches the {@code endHandler} BEFORE completing
 *     {@code bodyPromise}, so on the way down the promise is never complete. There is no base case
 *     here, so we go back to step 1.</li>
 * <li>The recursion ends only when the JVM throws {@code StackOverflowError}, which
 *     {@code ContextInternal.dispatch()} catches as {@code Throwable} and discards via
 *     {@code reportException()}. Then {@code handleEnd()} reaches {@code tryComplete()},
 *     and the request appears to succeed.</li>
 * </ul>
 * Because the error is swallowed, the request still returns the right answer, which is why
 * this has gone unnoticed. This test asserts on the recursion itself rather than on any
 * user-visible failure.
 */
@QuarkusTest
public class MultipartLoggerRecursionTest {
    @Inject
    @RestClient
    MyClient client;

    @BeforeEach
    void resetCounter() {
        CountingClientLogger.COUNTER.set(0);
    }

    @Test
    void plain() {
        assertEquals("hello", client.plain());

        assertEquals(1, CountingClientLogger.COUNTER.get());
    }

    @Test
    void multipart() {
        Payload payload = client.multipart();
        assertNotNull(payload);
        assertEquals("hello", payload.name);

        assertEquals(1, CountingClientLogger.COUNTER.get());
    }
}
