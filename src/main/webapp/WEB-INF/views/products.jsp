<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${currentCategory != null ? currentCategory.name : 'Каталог товаров'}"/>
<%@ include file="header.jsp" %>

<div class="container products-page">
    <h1>${currentCategory != null ? currentCategory.name : 'Каталог товаров'}</h1>
    
    <div class="products-layout">
        <!-- Боковая панель с категориями -->
        <aside class="categories-sidebar">
            <h3>Категории</h3>
            <nav class="categories-list">
                <a href="${pageContext.request.contextPath}/products" 
                   class="${empty currentCategory ? 'active' : ''}">
                    Все товары
                </a>
                <c:forEach var="cat" items="${categories}">
                    <a href="${pageContext.request.contextPath}/category?id=${cat.id}"
                       class="${currentCategory != null && currentCategory.id == cat.id ? 'active' : ''}">
                        ${cat.name}
                    </a>
                </c:forEach>
            </nav>
        </aside>
        
        <!-- Список товаров -->
        <section class="products-grid">
            <c:choose>
                <c:when test="${not empty products}">
                    <c:forEach var="product" items="${products}">
                        <div class="product-card">
                            <div class="product-image">
                                <span class="placeholder-img">📦</span>
                            </div>
                            <h3 class="product-title">${product.name}</h3>
                            <p class="product-price">${product.price} ₽</p>
                            <div class="product-actions">
                                <a href="${pageContext.request.contextPath}/product?id=${product.id}" 
                                   class="btn-secondary">Подробнее</a>
                                <c:if test="${sessionScope.user != null}">
                                    <form action="${pageContext.request.contextPath}/cart/add" method="post" 
                                          class="add-to-cart-form" style="display:inline;">
                                        <input type="hidden" name="productId" value="${product.id}">
                                        <button type="submit" class="btn-primary">В корзину</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p class="no-products">В этой категории пока нет товаров.</p>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</div>

<%@ include file="footer.jsp" %>
