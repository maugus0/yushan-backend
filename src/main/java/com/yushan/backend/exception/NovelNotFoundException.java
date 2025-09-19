package com.yushan.backend.exception;

/**
 * Thrown when a requested Novel entity cannot be found.
 *
 * <p>Why extend {@link Exception} (a checked exception)?
 * - Forces callers (service/controller layers) to consciously handle the
 *   not-found case via try/catch or method throws, making the control flow
 *   explicit instead of silently returning null.
 * - Improves API clarity and testability by distinguishing functional
 *   absence (business condition) from programming errors (runtime exceptions).
 * - Complies with static analysis rules (e.g., SpotBugs) that prefer
 *   meaningful, specific exception types over generic ones.
 */
public class NovelNotFoundException extends Exception {
    /**
     * Creates a new exception with a human-readable message describing which
     * novel could not be found (e.g., by ID or slug).
     *
     * @param message description of the missing resource
     */
    public NovelNotFoundException(String message) {
        super(message);
    }
}
