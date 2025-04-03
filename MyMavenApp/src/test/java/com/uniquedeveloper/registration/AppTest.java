package com.uniquedeveloper.registration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        
        // Set up H2 in-memory database
        testConnection = DriverManager.getConnection(
            "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table structure matching your MySQL schema
        try (Statement stmt = testConnection.createStatement()) {
            // First drop table if it exists from previous test
            stmt.execute("DROP TABLE IF EXISTS users");
            // Then create fresh table
            stmt.execute("CREATE TABLE users(uname VARCHAR(255), upwd VARCHAR(255), uemail VARCHAR(255))");
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
                // Check if table exists before trying to drop it
                try (Statement stmt = testConnection.createStatement();
                     ResultSet rs = stmt.executeQuery(
                         "SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'USERS'")) {
                    
                    if (rs.next()) { // Table exists
                        stmt.execute("DROP TABLE users");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error during teardown: " + e.getMessage());
            } finally {
                testConnection.close();
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
        
        // Force failure by dropping the table
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE users");
        }
        
        servlet.doPost(request, response);
        
        // Verify forward happened
        assertEquals("registration.jsp", response.getForwardedUrl());
        
        // Verify error attribute was set
        assertEquals("failed", request.getAttribute("status"));
    }
}
