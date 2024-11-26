package com.uniqedeveloper.registration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GuessGameServlet")
public class GuessGameServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // List of countries
    private static final List<String> COUNTRY_NAMES = Arrays.asList(
            "Germany", "France", "Italy", "Spain", "United States", 
            "Brazil", "Japan", "India", "Canada", "Australia"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if (action == null || action.equals("landing")) {
            // Redirect to Landing Page
            RequestDispatcher dispatcher = request.getRequestDispatcher("LandingPage.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (action.equals("newGame")) {
            // Initialize a new game
            session.setAttribute("score", 0);
            session.setAttribute("round", 1);
            startNewRound(session, request, response);
        } else if (action.equals("loadGame")) {
            // Mock load game logic
            session.setAttribute("score", 25);
            session.setAttribute("round", 3);
            startNewRound(session, request, response);
        } else if (action.equals("highScore")) {
            // Redirect to a mock high score page
            request.setAttribute("highScoreMessage", "High scores feature coming soon!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("LandingPage.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Retrieve the selected option and the correct answer
        String selectedOption = request.getParameter("selectedOption");
        String correctCountry = (String) session.getAttribute("correctCountry");

        // Check if the answer is correct
        boolean isCorrect = selectedOption.equals(correctCountry);

        // Update score and round
        int score = (int) session.getAttribute("score");
        int round = (int) session.getAttribute("round");

        if (isCorrect) {
            score += round * 5;
            session.setAttribute("score", score);
            request.setAttribute("message", "Correct! You earned " + (round * 5) + " points.");
        } else {
            request.setAttribute("message", "Wrong! The correct answer was: " + correctCountry);
        }

        // Check for game end condition
        if (round >= 10) {
            request.setAttribute("endGame", true);
            request.setAttribute("finalScore", score);
            session.invalidate();
        } else {
            session.setAttribute("round", round + 1);
            startNewRound(session, request, response);
        }
    }

    private void startNewRound(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Shuffle countries to generate options
        List<String> shuffledCountries = new ArrayList<>(COUNTRY_NAMES);
        Collections.shuffle(shuffledCountries);

        // Select correct country and three incorrect options
        String correctCountry = shuffledCountries.get(0);
        List<String> options = new ArrayList<>();
        options.add(correctCountry);
        for (int i = 1; i < 4; i++) {
            options.add(shuffledCountries.get(i));
        }
        Collections.shuffle(options);

        // Fetch flag URL for the correct country
        String flagUrl = getFlagUrl(correctCountry);

        // Set attributes for JSP
        session.setAttribute("correctCountry", correctCountry);
        request.setAttribute("flag", flagUrl);
        request.setAttribute("options", options);
        request.setAttribute("score", session.getAttribute("score"));
        request.setAttribute("round", session.getAttribute("round"));

        // Forward to game page
        RequestDispatcher dispatcher = request.getRequestDispatcher("GuessGame.jsp");
        dispatcher.forward(request, response);
    }

    // Fetch flag URL from REST Countries API
    private String getFlagUrl(String countryName) {
        String apiUrl = "https://restcountries.com/v3.1/name/" + countryName;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder jsonResponse = new StringBuilder();
                while (scanner.hasNext()) {
                    jsonResponse.append(scanner.nextLine());
                }
                scanner.close();

                JSONArray jsonArray = new JSONArray(jsonResponse.toString());
                JSONObject countryData = jsonArray.getJSONObject(0);
                return countryData.getJSONObject("flags").getString("png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; // Return empty URL if an error occurs
    }
}
