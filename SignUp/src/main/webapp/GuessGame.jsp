<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Guess the Country</title>
    <style>
        body, html {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100%;
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }
//
        .game-container {
            text-align: center;
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            width: 100%;
        }

        .flag-container img {
            max-width: 300px;
            height: auto;
            margin-bottom: 20px;
        }

        .options-container button {
            display: block;
            margin: 10px auto;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }

        .options-container button:hover {
            background-color: #45a049;
        }

        .score-container {
            margin-top: 20px;
            font-size: 18px;
            color: #333;
        }
    </style>
</head>
<body>
    <div class="game-container">
        <h1>Guess the Country</h1>
        <div class="flag-container">
            <img src="${flag}" alt="Country Flag">
        </div>
        <form method="post" action="GuessGameServlet">
            <div class="options-container">
                <c:forEach var="option" items="${options}">
                    <button type="submit" name="selectedOption" value="${option}">${option}</button>
                </c:forEach>
            </div>
        </form>
        <div class="score-container">
            <p>Score: ${score}</p>
        </div>
    </div>
</body>
</html>
