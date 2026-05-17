package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void setterAndGetterShouldWork() {
        Message message = new Message();

        message.setVal("hello");

        assertEquals("hello", message.getVal());
    }
}