<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Мои заказы"/>
<%@ include file="header.jsp" %>

<div class="container orders-page">
    <h1>История заказов</h1>
    
    <c:if test="${param.success == 'true'}">
        <div class="alert alert-success">Заказ успешно оформлен!</div>
    </c:if>
    
    <c:choose>
        <c:when test="${not empty orders}">
            <div class="orders-list">
                <c:forEach var="order" items="${orders}">
                    <div class="order-card">
                        <div class="order-header">
                            <span class="order-number">Заказ № ${order.orderNumber}</span>
                            <span class="order-status status-${order.status.toLowerCase()}">${order.status}</span>
                            <span class="order-date">${order.createdAt}</span>
                        </div>
                        
                        <div class="order-items">
                            <c:forEach var="item" items="${order.items}">
                                <div class="order-item">
                                    <span class="item-name">${item.productName}</span>
                                    <span class="item-quantity">${item.quantity} шт.</span>
                                    <span class="item-price">${item.subtotal} ₽</span>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <div class="order-footer">
                            <span class="order-total"><strong>Итого: ${order.totalAmount} ₽</strong></span>
                            <span class="order-address">📍 ${order.deliveryAddress}</span>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-orders">
                <p class="empty-orders-icon">📦</p>
                <p>У вас пока нет заказов</p>
                <a href="${pageContext.request.contextPath}/products" class="btn-primary">Перейти к покупкам</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="footer.jsp" %>
