<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>История заказов</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    <div class="container">
        <h1>История заказов</h1>
        <c:if test="${not empty orders}">
            <table>
                <tr>
                    <th>Номер заказа</th>
                    <th>Дата</th>
                    <th>Статус</th>
                    <th>Сумма</th>
                </tr>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>#${order.id}</td>
                        <td><fmt:formatDate value="${order.orderDate}" pattern="dd.MM.yyyy HH:mm"/></td>
                        <td>${order.status}</td>
                        <td>${order.total} руб.</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
        <c:if test="${empty orders}">
            <p>Нет заказов</p>
        </c:if>
    </div>
    <jsp:include page="/WEB-INF/views/footer.jsp"/>
</body>
</html>
