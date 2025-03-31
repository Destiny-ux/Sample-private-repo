package com.uniquedeveloper.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

        try (MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMock.when(() -> 
                DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Falcons?useSSL=false", 
                    "root", 
                    "RootRoot##"
                )
            ).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);
        }
    }

    @Test
    void testDoGet() throws Exception {
        when(request.getRequestDispatcher("registration.jsp")).thenReturn(requestDispatcher);
        
        servlet.doGet(request, response);
        
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostSuccessfulRegistration() throws Exception {
        try (MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMock.when(() -> 
                DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Falcons?useSSL=false", 
                    "root", 
                    "RootRoot##"
                )
            ).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            when(request.getParameter("name")).thenReturn("testuser");
            when(request.getParameter("email")).thenReturn("test@example.com");
            when(request.getParameter("pass")).thenReturn("password123");
            doNothing().when(response).sendRedirect("login.jsp");
            
            servlet.doPost(request, response);
            
            verify(preparedStatement).executeUpdate();
            verify(response).sendRedirect("login.jsp");
        }
    }

    @Test
    void testDoPostFailedRegistration() throws Exception {
        when(request.getParameter("name")).thenReturn("testuser");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("pass")).thenReturn("password123");
        
        when(preparedStatement.executeUpdate()).thenReturn(0);
        when(request.getRequestDispatcher("registration.jsp")).thenReturn(requestDispatcher);
        
        servlet.doPost(request, response);
        
        verify(request).setAttribute("status", "failed");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDatabaseConnection() {
        try (MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMock.when(() -> 
                DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Falcons?useSSL=false&allowPublicKeyRetrieval=true",
                    "root",
                    "RootRoot##"
                )
            ).thenReturn(connection);

            assertTrue(connection.isValid(1));
        }
    }
}
