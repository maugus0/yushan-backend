package com.yushan.backend.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Temporary unit test for {@link NovelNotFoundException}.
 *
 * Why this exists now:
 * - Provides minimal test coverage for the newly added exception so PR
 *   Quality Gate on SonarCloud does not report 0% coverage on new code.
 * - Verifies that the exception carries the provided message.
 *
 * IMPORTANT: This is a placeholder for CI needs.
 * - Please replace this with real, behavior-oriented tests when the
 *   corresponding feature is implemented, or delete it if no longer needed.
 */
class NovelNotFoundExceptionTest {

    @Test
    void constructorShouldStoreMessage() {
        String message = "Novel with id=123 not found";
        NovelNotFoundException ex = new NovelNotFoundException(message);
        assertEquals(message, ex.getMessage());
    }
}


