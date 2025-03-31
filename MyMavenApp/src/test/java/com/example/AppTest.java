package com.uniquedeveloper.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RegistrationServletTest {

    private RegistrationServlet servlet;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private RequestDispatcher requestDispatcher;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new RegistrationServlet();
        
        // Mock DriverManager behavior
        when(DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/Falcons?useSSL=false", 
            "root", 
            "RootRoot##"))
            .thenReturn(connection);
        
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate successful insert
    }

    @Test
    void testDoGet() throws Exception {
        when(request.getRequestDispatcher("registration.jsp")).thenReturn(requestDispatcher);
        
        servlet.doGet(request, response);
        
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostSuccessfulRegistration() throws Exception {
        // Setup mock request parameters
        when(request.getParameter("name")).thenReturn("testuser");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("pass")).thenReturn("password123");
        
        // Mock response sendRedirect
        doNothing().when(response).sendRedirect("login.jsp");
        
        servlet.doPost(request, response);
        
        // Verify database operations
        verify(preparedStatement).setString(1, "testuser");
        verify(preparedStatement).setString(2, "password123");
        verify(preparedStatement).setString(3, "test@example.com");
        verify(preparedStatement).executeUpdate();
        
        // Verify redirect to login page
        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void testDoPostFailedRegistration() throws Exception {
        // Setup mock request parameters
        when(request.getParameter("name")).thenReturn("testuser");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("pass")).thenReturn("password123");
        
        // Simulate failed database operation
        when(preparedStatement.executeUpdate()).thenReturn(0);
        when(request.getRequestDispatcher("registration.jsp")).thenReturn(requestDispatcher);
        
        servlet.doPost(request, response);
        
        // Verify failed status was set
        verify(request).setAttribute("status", "failed");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostDatabaseError() throws Exception {
        // Setup mock request parameters
        when(request.getParameter("name")).thenReturn("testuser");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("pass")).thenReturn("password123");
        
        // Simulate database error
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        when(request.getRequestDispatcher("registration.jsp")).thenReturn(requestDispatcher);
        
        servlet.doPost(request, response);
        
        // Verify error handling
        verify(requestDispatcher).forward(request, response);
    }
}
