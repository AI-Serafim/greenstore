<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${product.name}"/>
<%@ include file="header.jsp" %>

<div class="container product-details-page">
    <a href="${pageContext.request.contextPath}/products" class="back-link">← Назад в каталог</a>
    
    <div class="product-detail">
        <div class="product-detail-image">
            <span class="placeholder-img-large">📦</span>
        </div>
        
        <div class="product-detail-info">
            <h1>${product.name}</h1>
            <p class="product-category">Категория: ${product.categoryName}</p>
            <p class="product-price-large">${product.price} ₽</p>
            
            <div class="product-description">
                <h3>Описание</h3>
                <p>${product.description}</p>
            </div>
            
            <c:if test="${product.stockQuantity > 0}">
                <p class="stock-status in-stock">✓ В наличии: ${product.stockQuantity} шт.</p>
            </c:if>
            <c:if test="${product.stockQuantity <= 0}">
                <p class="stock-status out-of-stock">✗ Нет в наличии</p>
            </c:if>
            
            <c:choose>
                <c:when test="${sessionScope.user != null && product.stockQuantity > 0}">
                    <form action="${pageContext.request.contextPath}/cart/add" method="post" class="add-to-cart-form-large">
                        <input type="hidden" name="productId" value="${product.id}">
                        <div class="quantity-selector">
                            <label for="quantity">Количество:</label>
                            <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQuantity}">
                        </div>
                        <button type="submit" class="btn-primary btn-large">Добавить в корзину</button>
                    </form>
                </c:when>
                <c:when test="${sessionScope.user == null}">
                    <p class="login-prompt"><a href="${pageContext.request.contextPath}/login">Войдите</a> чтобы добавить товар в корзину</p>
                </c:when>
                <c:otherwise>
                    <button class="btn-disabled" disabled>Товар недоступен</button>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
