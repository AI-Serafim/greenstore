<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Вход</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    <div class="container">
        <h1>Вход</h1>
        <c:if test="${not empty param.error}">
            <p style="color: red;">Неверный email или пароль</p>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
            <br>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
            <br>
            <button type="submit">Войти</button>
        </form>
        <p>Нет аккаунта? <a href="${pageContext.request.contextPath}/register.jsp">Зарегистрируйтесь</a></p>
    </div>
    <jsp:include page="/WEB-INF/views/footer.jsp"/>
</body>
</html>
