<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Регистрация</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    <div class="container">
        <h1>Регистрация</h1>
        <c:if test="${not empty param.error}">
            <p style="color: red;">Пользователь с таким email уже существует</p>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/register" method="post">
            <label for="name">Имя:</label>
            <input type="text" id="name" name="name" required>
            <br>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
            <br>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
            <br>
            <button type="submit">Зарегистрироваться</button>
        </form>
        <p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login.jsp">Войдите</a></p>
    </div>
    <jsp:include page="/WEB-INF/views/footer.jsp"/>
</body>
</html>
