package com.uniqedeveloper.registration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuessGameServlet extends HttpServlet {
    private int score = 0; // Initialize score
    private int currentQuestion = 0; // Track the current question
    private static final String[][] QUESTIONS = {
        {"https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Flag_of_the_People%27s_Republic_of_China.svg/1200px-Flag_of_the_People%27s_Republic_of_China.svg.png", "China", "Japan", "India", "Russia"},
        {"https://upload.wikimedia.org/wikipedia/commons/0/00/Flag_of_Japan.svg", "Japan", "China", "India", "Germany"},
        {"https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Flag_of_India.svg/1200px-Flag_of_India.svg.png", "India", "USA", "Russia", "China"},
        {"https://upload.wikimedia.org/wikipedia/commons/0/03/Flag_of_Italy.svg", "Italy", "France", "Germany", "Japan"}
    };

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedOption = request.getParameter("selectedOption");

        // Check if the selected option is correct
        if (selectedOption != null && selectedOption.equals(QUESTIONS[currentQuestion][1])) {
            score += 10; // Increment score by 10
        }

        // Move to the next question or end the game
        currentQuestion++;
        if (currentQuestion >= QUESTIONS.length) {
            // End of game
            request.setAttribute("score", score);
            request.setAttribute("message", "Game Over! Your Final Score is: " + score);
            RequestDispatcher dispatcher = request.getRequestDispatcher("GameOver.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Pass updated data to the JSP
        request.setAttribute("flag", QUESTIONS[currentQuestion][0]);
        request.setAttribute("options", new String[] {
            QUESTIONS[currentQuestion][1],
            QUESTIONS[currentQuestion][2],
            QUESTIONS[currentQuestion][3],
            QUESTIONS[currentQuestion][4]
        });
        request.setAttribute("score", score);

        // Forward to the JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("GuessGame.jsp");
        dispatcher.forward(request, response);
    }
}
