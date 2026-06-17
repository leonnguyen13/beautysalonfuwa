<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo cáo - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-3">
</head>
<body>
    <div class="admin-shell">
        <c:set var="activePage" value="reports"/>
        <%@include file="sidebar.jsp"%>

        <main class="admin-main">
            <div class="admin-topbar">
                <div>
                    <p class="eyebrow">Thống kê</p>
                    <h1>Báo cáo</h1>
                </div>
                <a class="btn btn-outline" href="#">Xuất báo cáo</a>
            </div>

            <section class="stats-grid">
                <c:forEach var="summary" items="${summaries}">
                    <article class="stat-card">
                        <span>${summary.title}</span>
                        <strong>${summary.value}</strong>
                        <small>${summary.note}</small>
                    </article>
                </c:forEach>
            </section>

            <section class="table-panel">
                <h2>Dịch vụ phổ biến</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Dịch vụ</th>
                            <th>Danh mục</th>
                            <th>Lượt đặt</th>
                            <th>Doanh thu</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="service" items="${popularServices}">
                            <tr>
                                <td>${service.id}</td>
                                <td>${service.name}</td>
                                <td>${service.category}</td>
                                <td>${service.description}</td>
                                <td>${service.priceText}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</body>
</html>
