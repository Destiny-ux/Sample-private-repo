package com.uniquedeveloper.registration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private RegistrationServlet servlet;
    private Connection testConnection;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize servlet
        servlet = new RegistrationServlet();
        
        // Set up H2 in-memory database with MySQL compatibility mode
        testConnection = DriverManager.getConnection(
            "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL", "sa", "");
        
        // Create test table structure matching production
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users(" +
                "uname VARCHAR(255), " +
                "upwd VARCHAR(255), " +
                "uemail VARCHAR(255))");
        }
        
        // Inject the test connection
        servlet.setTestConnection(testConnection);
        
        // Initialize mock servlet objects
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (testConnection != null && !testConnection.isClosed()) {
            try {
                testConnection.close();
            } catch (Exception e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    @Test
    public void testDoGet() throws Exception {
        servlet.doGet(request, response);
        assertEquals("registration.jsp", response.getForwardedUrl());
    }

    @Test
    public void testDoPostSuccessfulRegistration() throws Exception {
        // Set request parameters
        request.addParameter("name", "testuser");
        request.addParameter("email", "test@example.com");
        request.addParameter("pass", "password123");
        
        servlet.doPost(request, response);
        
        // Verify redirect happened
        assertEquals("login.jsp", response.getRedirectedUrl());
        
        // Verify data was actually inserted
        try (Statement stmt = testConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            assertTrue(rs.next());
            assertEquals("testuser", rs.getString("uname"));
            assertEquals("test@example.com", rs.getString("uemail"));
            assertEquals("password123", rs.getString("upwd"));
            assertFalse(rs.next()); // Only one record should exist
        }
    }

    @Test
    public void testDoPostFailedRegistration() throws Exception {
        // Set request parameters
        request.addParameter("name", "testuser");
        request.addParameter("email", "test@example.com");
        request.addParameter("pass", "password123");
        
        // Force failure by closing connection first
        testConnection.close();
        
        servlet.doPost(request, response);
        
        // Verify forward happened
        assertEquals("registration.jsp", response.getForwardedUrl());
        
        // Verify error attribute was set
        assertEquals("failed", request.getAttribute("status"));
    }
}
