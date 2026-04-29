<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Товары</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    <div class="container">
        <h1>Товары</h1>
        <div>
            <h2>Категории:</h2>
            <a href="${pageContext.request.contextPath}/products">Все товары</a>
            <c:forEach var="category" items="${categories}">
                <a href="${pageContext.request.contextPath}/products/category?categoryId=${category.id}">${category.name}</a>
            </c:forEach>
        </div>
        <div>
            <c:forEach var="product" items="${products}">
                <div class="product-card">
                    <h2>${product.name}</h2>
                    <p>${product.description}</p>
                    <p>Цена: ${product.price} руб.</p>
                    <a href="${pageContext.request.contextPath}/products/details?id=${product.id}">Подробнее</a>
                    <c:if test="${not empty sessionScope.user}">
                        <form action="${pageContext.request.contextPath}/cart/add" method="post">
                            <input type="hidden" name="productId" value="${product.id}">
                            <label for="quantity">Количество:</label>
                            <input type="number" id="quantity" name="quantity" value="1" min="1">
                            <button type="submit">В корзину</button>
                        </form>
                    </c:if>
                </div>
            </c:forEach>
        </div>
    </div>
    <jsp:include page="/WEB-INF/views/footer.jsp"/>
</body>
</html>
