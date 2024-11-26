<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Guess the Country</title>
    <link rel="stylesheet" href="guess-style.css">
</head>
<body>
    <div class="game-container">
        <div class="score">Score: ${score}</div>
        <div class="flag-container">
            <img src="${flag}" alt="Country Flag">
        </div>
        <form method="post" action="GuessGameServlet">
            <div class="options-container">
                <c:forEach var="option" items="${options}">
                    <button type="submit" name="selectedOption" value="${option}" class="option">
                        ${option}
                    </button>
                </c:forEach>
            </div>
        </form>
    </div>
</body>
</html>
