<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Регистрация"/>
<%@ include file="header.jsp" %>

<div class="auth-container">
    <div class="auth-card">
        <h1>Регистрация</h1>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/register" method="post" class="auth-form">
            <div class="form-row">
                <div class="form-group">
                    <label for="firstName">Имя:</label>
                    <input type="text" id="firstName" name="firstName" required 
                           placeholder="Иван" value="${param.firstName}">
                </div>
                
                <div class="form-group">
                    <label for="lastName">Фамилия:</label>
                    <input type="text" id="lastName" name="lastName" required 
                           placeholder="Петров" value="${param.lastName}">
                </div>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required 
                       placeholder="your@email.com" value="${param.email}">
            </div>
            
            <div class="form-group">
                <label for="password">Пароль:</label>
                <input type="password" id="password" name="password" required 
                       placeholder="Минимум 6 символов" minlength="6">
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">Подтверждение пароля:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required 
                       placeholder="Повторите пароль">
            </div>
            
            <button type="submit" class="btn-primary btn-full">Зарегистрироваться</button>
        </form>
        
        <p class="auth-link">
            Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login">Войти</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>
