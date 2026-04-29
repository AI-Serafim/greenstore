<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Корзина</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    <div class="container">
        <h1>Корзина</h1>
        <c:if test="${not empty param.error}">
            <c:choose>
                <c:when test="${param.error == 'empty'}">
                    <p style="color: red;">Корзина пуста. Добавьте товары перед оформлением заказа.</p>
                </c:when>
                <c:otherwise>
                    <p style="color: red;">Произошла ошибка при выполнении операции</p>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${not empty cartItems}">
            <table>
                <tr>
                    <th>Товар</th>
                    <th>Цена</th>
                    <th>Количество</th>
                    <th>Сумма</th>
                    <th>Действия</th>
                </tr>
                <c:forEach var="item" items="${cartItems}">
                    <tr>
                        <td>${item.product.name}</td>
                        <td>${item.product.price} руб.</td>
                        <td>${item.quantity}</td>
                        <td>${item.product.price * item.quantity} руб.</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/cart/remove" method="post">
                                <input type="hidden" name="productId" value="${item.productId}">
                                <button type="submit">Удалить</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <p><strong>Общая сумма: ${total} руб.</strong></p>
            <form action="${pageContext.request.contextPath}/order/create" method="post">
                <button type="submit">Оформить заказ</button>
            </form>
        </c:if>
        <c:if test="${empty cartItems}">
            <p>Корзина пуста</p>
        </c:if>
    </div>
    <jsp:include page="/WEB-INF/views/footer.jsp"/>
</body>
</html>
