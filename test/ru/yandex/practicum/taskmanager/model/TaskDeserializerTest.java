package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.taskmanager.service.TaskDeserializer.escapeCsv;
import static ru.yandex.practicum.taskmanager.service.TaskDeserializer.unescapeCsv;

class TaskDeserializerTest {

    @Test
    void testEscapeCsv() {
        assertAll("CSV escaping should work correctly",
                () -> assertEquals("", escapeCsv(null), "Null should be escaped as empty string"),
                () -> assertEquals("simple", escapeCsv("simple"), "Simple string should not be escaped"),
                () -> assertEquals("\"comma,value\"", escapeCsv("comma,value"), "Comma should be escaped"),
                () -> assertEquals("\"quote\"\"value\"", escapeCsv("quote\"value"), "Quotes should be escaped")
        );
    }

    @Test
    void testUnescapeCsv() {
        assertAll("CSV unescaping should work correctly",
                () -> assertEquals("", unescapeCsv(null), "Null should be unescaped as empty string"),
                () -> assertEquals("simple", unescapeCsv("simple"), "Simple string should not be unescaped"),
                () -> assertEquals("comma,value", unescapeCsv("\"comma,value\""), "Comma should be unescaped"),
                () -> assertEquals("quote\"value", unescapeCsv("\"quote\"\"value\""), "Quotes should be unescaped")
        );
    }

}