<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Вход в аккаунт"/>
<%@ include file="header.jsp" %>

<div class="auth-container">
    <div class="auth-card">
        <h1>Вход в аккаунт</h1>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/login" method="post" class="auth-form">
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required 
                       placeholder="your@email.com" value="${param.email}">
            </div>
            
            <div class="form-group">
                <label for="password">Пароль:</label>
                <input type="password" id="password" name="password" required 
                       placeholder="Введите пароль">
            </div>
            
            <button type="submit" class="btn-primary btn-full">Войти</button>
        </form>
        
        <p class="auth-link">
            Нет аккаунта? <a href="${pageContext.request.contextPath}/register">Зарегистрироваться</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>
