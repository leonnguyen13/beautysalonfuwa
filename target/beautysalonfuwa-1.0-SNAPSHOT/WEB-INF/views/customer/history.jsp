<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử đặt lịch - FUWA Beauty Salon</title>
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
                <a href="${pageContext.request.contextPath}/services">Dịch vụ</a>
                <a href="${pageContext.request.contextPath}/booking">Đặt lịch</a>
                <a class="active" href="${pageContext.request.contextPath}/my-appointments">Lịch sử</a>
            </nav>
            <div class="nav-actions">
                <c:choose>
                    <c:when test="${sessionScope.role == 'ADMIN' || sessionScope.role == 'MANAGER' || sessionScope.role == 'STAFF'}">
                        <a class="account-chip" href="${pageContext.request.contextPath}/admin/dashboard">${sessionScope.fullName}</a>
                    </c:when>
                    <c:otherwise>
                        <a class="account-chip" href="${pageContext.request.contextPath}/my-appointments">${sessionScope.fullName}</a>
                    </c:otherwise>
                </c:choose>
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/booking">Đặt lịch</a>
            </div>
        </div>
    </header>

    <main>
        <section class="page-hero">
            <div class="container">
                <p class="eyebrow">Lịch sử</p>
                <h1>Lịch sử đặt lịch</h1>
            </div>
        </section>

        <section class="section">
            <div class="container">
                <section class="table-panel">
                    <h2>Lịch hẹn của bạn</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Dịch vụ</th>
                                <th>Nhân viên</th>
                                <th>Ngày</th>
                                <th>Giờ</th>
                                <th>Thanh toán</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="appointment" items="${appointments}">
                                <tr>
                                    <td>${appointment.id}</td>
                                    <td>${appointment.service}</td>
                                    <td>${appointment.employee}</td>
                                    <td>${appointment.appointmentDate}</td>
                                    <td>${appointment.appointmentTime}</td>
                                    <td>${appointment.paymentMethod}</td>
                                    <td>
                                        <span class="badge">
                                            <c:choose>
                                                <c:when test="${appointment.status == 'PENDING'}">Chờ xác nhận</c:when>
                                                <c:when test="${appointment.status == 'CONFIRMED'}">Đã xác nhận</c:when>
                                                <c:when test="${appointment.status == 'PROCESSING'}">Đang xử lý</c:when>
                                                <c:when test="${appointment.status == 'COMPLETED'}">Hoàn thành</c:when>
                                                <c:when test="${appointment.status == 'CANCELLED'}">Đã hủy</c:when>
                                                <c:otherwise>${appointment.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty appointments}">
                                <tr>
                                    <td colspan="7">Bạn chưa có lịch hẹn nào.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </section>
            </div>
        </section>
    </main>
</body>
</html>
