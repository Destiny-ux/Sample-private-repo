package cscorner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String uemail = request.getParameter("username");
		String upwd = request.getParameter("password");
		HttpSession session = request.getSession();
		RequestDispatcher dispatcher = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // 使用更新的驱动类
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Falcons?useSSL=false",
					"root", "RootRoot##");
					PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE uemail = ? AND upwd = ?")) {
				pst.setString(1, uemail);
				pst.setString(2, upwd);

				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					session.setAttribute("name", rs.getString("uname"));
					dispatcher = request.getRequestDispatcher("index.jsp");
				} else {
					request.setAttribute("status", "failed");
					dispatcher = request.getRequestDispatcher("login.jsp");
				}
				dispatcher.forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace(); // 你可以考虑使用日志记录
			request.setAttribute("status", "error");
			dispatcher = request.getRequestDispatcher("login.jsp");
			dispatcher.forward(request, response);
		}
	}
}
