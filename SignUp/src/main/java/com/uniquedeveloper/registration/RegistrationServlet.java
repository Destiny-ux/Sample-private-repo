package com.uniquedeveloper.registration;

import java.io.IOException;
import java.io.PrintWriter;
//import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import java.io.PrintWriter;

/**
 * Servlet implementation class Registrationservlet
 */
@WebServlet("/register")
public class Registrationservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String uname= request.getParameter("name");
		String uemail=request.getParameter("email");
		String upwd=request.getParameter("pass");
		String umobile=request.getParameter("contact");
		RequestDispatcher dispatcher=null;
		Connection con=null;
		PrintWriter out= response.getWriter();
		out.print("working ");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		 
			 con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Falcons","root","RootRoot##");
			PreparedStatement pst=con.prepareStatement("insert into users(uname,upwd,uemail,umobile)values(?,?,?,?)");
		    pst.setString(1, uname );
		    pst.setString(2, upwd );
		    pst.setString(3, uemail );
		    pst.setString(4, umobile );
		    
		   int rowCount= pst.executeUpdate();
		   dispatcher=request.getRequestDispatcher("NewFile.jsp");
		   if (rowCount>0) {
			request.setAttribute("status", "success");
		}
		   else {
			   request.setAttribute("status", "failed");
		}
		   dispatcher.forward(request, response);
		
		}catch (Exception e) {
		e.printStackTrace();
		}finally {
		//if (con!=null) {}
		try {
			con.close();
		} 
		catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	}
}
