<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>${product.name}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    <div class="container">
        <h1>${product.name}</h1>
        <p>${product.description}</p>
        <p>Цена: ${product.price} руб.</p>
        <c:if test="${not empty sessionScope.user}">
            <form action="${pageContext.request.contextPath}/cart/add" method="post">
                <input type="hidden" name="productId" value="${product.id}">
                <label for="quantity">Количество:</label>
                <input type="number" id="quantity" name="quantity" value="1" min="1">
                <button type="submit">В корзину</button>
            </form>
        </c:if>
        <a href="${pageContext.request.contextPath}/products">Вернуться к каталогу</a>
    </div>
    <jsp:include page="/WEB-INF/views/footer.jsp"/>
</body>
</html>
