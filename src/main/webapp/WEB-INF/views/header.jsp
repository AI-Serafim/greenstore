<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <nav>
        <a href="${pageContext.request.contextPath}/products">Главная</a>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/cart">Корзина</a>
                <a href="${pageContext.request.contextPath}/order">История заказов</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Выйти</a>
                <span>Привет, ${sessionScope.user.name}!</span>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp">Войти</a>
                <a href="${pageContext.request.contextPath}/register.jsp">Зарегистрироваться</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
