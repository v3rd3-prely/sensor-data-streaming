package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Message}.
 * Verifies basic setter and getter functionality for message content.
 *
 * @author Your Name
 */
public final class MessageTest {

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private MessageTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that the message value can be set and retrieved correctly.
     * Verifies that {@link Message#setVal(String)} stores the value and
     * {@link Message#getVal()} returns the expected string.
     *
     * @see Message#setVal(String)
     * @see Message#getVal()
     */
    @Test
    void setterAndGetterShouldWork() {
        Message message = new Message();

        message.setVal("hello");

        assertEquals("hello", message.getVal());
    }
}
