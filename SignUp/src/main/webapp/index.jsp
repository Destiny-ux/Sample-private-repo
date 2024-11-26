<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <title>Freelancer - Start Bootstrap Theme</title>
    <style>
      /* 样式在此设置 */
      body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 0;
        background-color: #000000;
        color: orange; /* 设置整体文字颜色为橘黄色 */
      }

      header {
        background-color: #000015;
        color: white;
        padding: 15px;
        text-align: left;
      }

      .header-nav a {
        color: orange; /* 设置链接颜色为橘黄色 */
        padding: 14px 20px;
        text-decoration: none;
        display: inline-block;
      }

      .header-nav a:hover {
        background-color: white;
      }

      .hero {
        text-align: center;
        padding: 100px 20px;
        background-color: #282A35;
        color: orange; /* 设置英雄部分文字颜色为橘黄色 */
      }

      .games, .contact {
        display: flex;
        justify-content: space-around;
        padding: 20px;
      }

      .game-list, .contact-list {
        text-align: center;
      }

      /* 设置地址和电话文字颜色为橘黄色 */
      .contact-list p {
        color: white;
      }

      /* 设置游戏标题颜色为橘黄色 */
      .game-list h3 {
        color: orange;
      }

      /* 设置其他标题文字颜色为橘黄色 */
      h1 {
        color: orange;
      }

      h2 {
        color: white;
      }

      h5 {
        color: white;
      }

      /* 增加游戏列表和联系方式部分之间的间距 */
      .games {
        margin-bottom: 50px; /* 增加底部间距 */
      }

      /* 设置联系方式部分的上边距 */
      .contact {
        margin-top: 5px; /* 增加顶部间距 */
      }

      /* 调整Reach out to us标题的样式 */
      #contact {
        text-align: center; /* 使标题居中 */
        margin-top: 300px; /* 向下移动标题 */
      }

      .hero {
        background-image: url("images/falcon.jpg");
        background-size: 80%; /* 使图片覆盖整个区域 */
        background-position: center; /* 背景图片居中显示 */
        height: 10vh; /* 设置 section 高度为视口高度 */
        display: flex; /* 使用 flexbox 布局 */
        justify-content: center; /* 水平居中 */
        align-items: center; /* 垂直居中 */

        position: relative;
      }

      .hero::after {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5); /* 半透明黑色，调节透明度 */
        z-index: 1; /* 确保背景层位于图片上方 */
      }

      .hero h1 {
        z-index: 2; /* 文字位于覆盖层上方 */
        color: orange; /* 设置文字颜色为白色，以便在暗背景上清晰可见 */
        position: relative; /* 确保 h1 在同一个层级 */
        font-size: 4rem;
      }
    </style>
  </head>
  <body>
    <header>
      <div class="header-nav">
        <!-- 添加指向指定页面的链接 -->
        <a href="#home">Falcons</a>
        <a href="http://localhost:8082/QuizLogin/registration.jsp">Sign up</a> <!-- 跳转到注册页面 -->
        <a href="http://localhost:8082/QuizLogin/login.jsp">Sign in</a> <!-- 跳转到登录页面 -->
        <a href="#games">Games</a>
        <a href="#contact">Contact</a>
      </div>
    </header>

    <section class="hero">
      <h1>Welcome to the Games Center</h1>
    </section>

    <main>
      <h2 class="title" id="games">List of Games</h2>
      <div class="games">
        <div class="game-list">
          <a href="monster-game.html"> <!-- 链接到游戏页面 -->
            <img
              src="https://spaces.w3schools.com/images/monster.svg"
              alt="monster"
              style="width:100px; height: 100px;"
            />
            <h3>Monster</h3>
          </a>
        </div>
        <div class="game-list">
          <a href="joker-game.html"> <!-- 链接到游戏页面 -->
            <img
              src="https://spaces.w3schools.com/images/joker.svg"
              alt="joker"
              style="width:100px; height: 100px;"
            />
            <h3>Joker</h3>
          </a>
        </div>
        <div class="game-list">
          <a href="halloween-game.html"> <!-- 链接到游戏页面 -->
            <img
              src="https://spaces.w3schools.com/images/halloween.svg"
              alt="halloween"
              style="width:100px; height: 100px;"
            />
            <h3>Halloween</h3>
          </a>
        </div>
      </div>

      <h2 class="title" id="contact">Reach out to us</h2>
      <div class="contact">
        <div class="contact-list">
          <h5>Address</h5>
          <p>Technological University of the Shannon</p>
          <p>Athlone Campus</p>
          <p>University Road, Athlone</p>
        </div>

        <div class="contact-list">
          <h5>Phone</h5>
          <p>763-957-8888</p>
          <p>706-734-9999</p>
        </div>
      </div>
    </main>
  </body>
</html>
