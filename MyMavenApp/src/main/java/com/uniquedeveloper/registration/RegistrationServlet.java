package com.uniquedeveloper.registration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // For production use
    protected Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/Falcons?useSSL=false&allowPublicKeyRetrieval=true", 
            "root", 
            "RootRoot##");
    }
    
    // For testing use
    protected Connection testConnection;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("registration.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uname = request.getParameter("name");
        String uemail = request.getParameter("email");
        String upwd = request.getParameter("pass");

        Connection con = null;
        RequestDispatcher dispatcher = request.getRequestDispatcher("registration.jsp");

        try {
            con = testConnection != null ? testConnection : getConnection();
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO users(uname, upwd, uemail) VALUES (?, ?, ?)");
            
            pst.setString(1, uname);
            pst.setString(2, upwd);
            pst.setString(3, uemail);

            int rowCount = pst.executeUpdate();
            
            if (rowCount > 0) {
                response.sendRedirect("login.jsp");
                return; // Important to return after redirect
            } 
            
            request.setAttribute("status", "failed");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("status", "failed");
        } finally {
            if (con != null && testConnection == null) { // Only close non-test connections
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        dispatcher.forward(request, response);
    }
    
    // For testing purposes only
    void setTestConnection(Connection connection) {
        this.testConnection = connection;
    }
}
