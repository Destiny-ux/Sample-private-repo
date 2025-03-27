package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the application
 */
@DisplayName("Application Tests")
public class AppTest {

    @BeforeEach
    public void setUp() {
        // Setup code that runs before each test
    }

    @Test
    @DisplayName("Basic Truth Test")
    public void testBasicAssertion() {
        assertTrue(true, "Basic truth assertion should pass");
    }

    @Test
    @DisplayName("Simple Math Test")
    public void testSimpleMath() {
        int result = 2 + 2;
        assertEquals(4, result, "2 + 2 should equal 4");
    }

    @Test
    @DisplayName("String Comparison Test")
    public void testStringComparison() {
        String hello = "Hello";
        String world = "World";
        assertNotEquals(hello, world, "Strings should not be equal");
    }

    @Test
    @DisplayName("Exception Test")
    public void testExceptionThrowing() {
        assertThrows(ArithmeticException.class, () -> {
            int result = 1 / 0;
        }, "Should throw ArithmeticException");
    }

    @Test
    @DisplayName("Array Test")
    public void testArrayEquality() {
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};
        assertArrayEquals(expected, actual, "Arrays should be equal");
    }
}
