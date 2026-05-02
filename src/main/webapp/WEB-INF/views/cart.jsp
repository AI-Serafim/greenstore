<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Корзина"/>
<%@ include file="header.jsp" %>

<div class="container cart-page">
    <h1>Корзина покупок</h1>
    
    <c:choose>
        <c:when test="${not empty cartItems}">
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>Товар</th>
                        <th>Цена</th>
                        <th>Количество</th>
                        <th>Сумма</th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${cartItems}">
                        <tr>
                            <td>
                                <span class="product-icon">📦</span>
                                ${item.productName}
                            </td>
                            <td>${item.price} ₽</td>
                            <td>${item.quantity} шт.</td>
                            <td>${item.subtotal} ₽</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/remove" method="post" style="display:inline;">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <button type="submit" class="btn-remove">Удалить</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" class="text-right"><strong>Итого:</strong></td>
                        <td colspan="2"><strong class="total-amount">${total} ₽</strong></td>
                    </tr>
                </tfoot>
            </table>
            
            <div class="cart-actions">
                <a href="${pageContext.request.contextPath}/products" class="btn-secondary">Продолжить покупки</a>
                <a href="${pageContext.request.contextPath}/checkout" class="btn-primary btn-large">Оформить заказ</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-cart">
                <p class="empty-cart-icon">🛒</p>
                <p>Ваша корзина пуста</p>
                <a href="${pageContext.request.contextPath}/products" class="btn-primary">Перейти в каталог</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="footer.jsp" %>
