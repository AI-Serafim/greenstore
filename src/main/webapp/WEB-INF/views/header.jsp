<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'GreenStore'}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header class="main-header">
        <div class="container header-content">
            <a href="${pageContext.request.contextPath}/products" class="logo">
                🌿 GreenStore
            </a>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/products">Каталог</a>
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <a href="${pageContext.request.contextPath}/cart">Корзина</a>
                        <a href="${pageContext.request.contextPath}/orders">Заказы</a>
                        <span class="user-info">👤 <c:out value="${sessionScope.user.firstName}"/></span>
                        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Выход</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login">Вход</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn-primary">Регистрация</a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>
    <main class="main-content">
