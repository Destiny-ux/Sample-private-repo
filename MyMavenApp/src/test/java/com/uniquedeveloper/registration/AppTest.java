package com.uniquedeveloper.registration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private RegistrationServlet servlet;
    private Connection realConnection;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockRequestDispatcher requestDispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize servlet
        servlet = new RegistrationServlet();
        
        // Set up H2 in-memory database
        realConnection = DriverManager.getConnection(
            "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table
        try (Statement stmt = realConnection.createStatement()) {
            stmt.execute("CREATE TABLE users(name VARCHAR(255), email VARCHAR(255), pass VARCHAR(255))");
        }
        
        // Inject the test connection
        servlet.setConnection(realConnection);
        
        // Initialize mock servlet objects
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        requestDispatcher = new MockRequestDispatcher("registration.jsp");
        
        // Configure request dispatcher
        request.setRequestDispatcher(requestDispatcher);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (realConnection != null) {
            try (Statement stmt = realConnection.createStatement()) {
                stmt.execute("DROP TABLE users");
            }
            realConnection.close();
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
        try (var stmt = realConnection.createStatement();
             var rs = stmt.executeQuery("SELECT * FROM users")) {
            assertTrue(rs.next());
            assertEquals("testuser", rs.getString("name"));
            assertEquals("test@example.com", rs.getString("email"));
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
        try (Statement stmt = realConnection.createStatement()) {
            stmt.execute("DROP TABLE users");
        }
        
        servlet.doPost(request, response);
        
        // Verify forward happened
        assertEquals("registration.jsp", response.getForwardedUrl());
        
        // Verify error attribute was set
        assertEquals("failed", request.getAttribute("status"));
    }

    @Test
    public void testDatabaseConnection() throws Exception {
        assertTrue(realConnection.isValid(1));
    }
}
