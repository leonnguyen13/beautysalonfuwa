<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dịch vụ - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <header class="site-header">
        <div class="nav-wrap">
            <a class="brand" href="${pageContext.request.contextPath}/"><span class="brand-mark">FW</span> FUWA Salon</a>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/">Trang chủ</a>
                <a class="active" href="${pageContext.request.contextPath}/services">Dịch vụ</a>
                <a href="${pageContext.request.contextPath}/booking">Đặt lịch</a>
                <c:if test="${not empty sessionScope.authUser}">
                    <a href="${pageContext.request.contextPath}/my-appointments">Lịch sử</a>
                </c:if>
            </nav>
            <div class="nav-actions">
                <c:choose>
                    <c:when test="${not empty sessionScope.authUser}">
                        <c:choose>
                            <c:when test="${sessionScope.role == 'ADMIN' || sessionScope.role == 'MANAGER' || sessionScope.role == 'STAFF'}">
                                <a class="account-chip" href="${pageContext.request.contextPath}/admin/dashboard">${sessionScope.fullName}</a>
                            </c:when>
                            <c:otherwise>
                                <a class="account-chip" href="${pageContext.request.contextPath}/my-appointments">${sessionScope.fullName}</a>
                            </c:otherwise>
                        </c:choose>
                        <a class="btn btn-outline" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-outline" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    </c:otherwise>
                </c:choose>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/booking">Đặt lịch</a>
            </div>
        </div>
    </header>

    <main>
        <section class="page-hero">
            <div class="container">
                <p class="eyebrow">Dịch vụ</p>
                <h1>Bảng dịch vụ FUWA</h1>
            </div>
        </section>

        <section class="section">
            <div class="container service-grid">
                <c:forEach var="service" items="${services}">
                    <article class="service-card">
                        <img src="${service.imageUrl}" alt="${service.category}">
                        <div class="card-body">
                            <h3>${service.name}</h3>
                            <p>${service.description}</p>
                            <div class="price-row">
                                <span>${service.priceText}</span>
                                <a href="${pageContext.request.contextPath}/booking">Chọn</a>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </section>
    </main>
</body>
</html>
