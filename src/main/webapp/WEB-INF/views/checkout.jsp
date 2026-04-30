<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Оформление заказа"/>
<%@ include file="header.jsp" %>

<div class="container checkout-page">
    <h1>Оформление заказа</h1>
    
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    
    <div class="checkout-layout">
        <!-- Информация о заказе -->
        <section class="order-summary">
            <h2>Ваш заказ</h2>
            <table class="summary-table">
                <thead>
                    <tr>
                        <th>Товар</th>
                        <th>Кол-во</th>
                        <th>Сумма</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${cartItems}">
                        <tr>
                            <td>${item.productName}</td>
                            <td>${item.quantity}</td>
                            <td>${item.subtotal} ₽</td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="2"><strong>Итого:</strong></td>
                        <td><strong>${total} ₽</strong></td>
                    </tr>
                </tfoot>
            </table>
        </section>
        
        <!-- Форма оформления -->
        <section class="checkout-form-section">
            <h2>Данные доставки</h2>
            <form action="${pageContext.request.contextPath}/checkout" method="post" class="checkout-form">
                <div class="form-group">
                    <label for="deliveryAddress">Адрес доставки *</label>
                    <textarea id="deliveryAddress" name="deliveryAddress" rows="3" required 
                              placeholder="Город, улица, дом, квартира">${param.deliveryAddress}</textarea>
                </div>
                
                <div class="form-group">
                    <label for="contactPhone">Контактный телефон *</label>
                    <input type="tel" id="contactPhone" name="contactPhone" required 
                           placeholder="+7 (___) ___-__-__" value="${param.contactPhone}">
                </div>
                
                <div class="form-group">
                    <label for="notes">Комментарий к заказу</label>
                    <textarea id="notes" name="notes" rows="2" 
                              placeholder="Пожелания к доставке">${param.notes}</textarea>
                </div>
                
                <div class="checkout-actions">
                    <a href="${pageContext.request.contextPath}/cart" class="btn-secondary">Назад в корзину</a>
                    <button type="submit" class="btn-primary btn-large">Подтвердить заказ</button>
                </div>
            </form>
        </section>
    </div>
</div>

<%@ include file="footer.jsp" %>
