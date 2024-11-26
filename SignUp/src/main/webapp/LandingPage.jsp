<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Guess the Country Game</title>
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f4f4f4;
        }

        .menu-container {
            text-align: center;
        }

        button {
            margin: 10px;
            padding: 10px 20px;
            font-size: 18px;
            cursor: pointer;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 8px;
            transition: all 0.3s ease;
        }

        button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="menu-container">
        <h1>Welcome to Guess the Country Game!</h1>
        <form action="GuessGameServlet" method="get">
            <button name="action" value="newGame">New Game</button>
            <button name="action" value="loadGame">Load Saved Game</button>
            <button name="action" value="highScore">High Scores</button>
        </form>
    </div>
</body>
</html>
