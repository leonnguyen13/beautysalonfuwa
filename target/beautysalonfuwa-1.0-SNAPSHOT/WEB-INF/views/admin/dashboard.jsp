<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tổng quan quản trị - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-3">
</head>
<body>
    <div class="admin-shell">
        <c:set var="activePage" value="dashboard"/>
        <%@include file="sidebar.jsp"%>
        <main class="admin-main">
            <div class="admin-topbar">
                <div>
                    <p class="eyebrow">Quản lý</p>
                    <h1>Tổng quan salon</h1>
                </div>
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/">Về trang khách</a>
            </div>

            <section class="stats-grid">
                <article class="stat-card">
                    <span>Tổng lịch hẹn</span>
                    <strong>${todayAppointmentCount}</strong>
                </article>
                <article class="stat-card">
                    <span>Tổng doanh thu</span>
                    <strong>${monthlyRevenue}</strong>
                </article>
                <article class="stat-card">
                    <span>Tổng khách hàng</span>
                    <strong>${newCustomerCount}</strong>
                </article>
            </section>

            <section class="table-panel">
                <h2>Lịch hẹn gần nhất</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Khách hàng</th>
                            <th>Dịch vụ</th>
                            <th>Nhân viên</th>
                            <th>Thời gian</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${recentAppointments}">
                            <tr>
                                <td>${appointment.fullName}</td>
                                <td>${appointment.service}</td>
                                <td>${appointment.employee}</td>
                                <td>${appointment.appointmentTime} · ${appointment.appointmentDate}</td>
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
                        <c:if test="${empty recentAppointments}">
                            <tr>
                                <td colspan="5">Chưa có lịch hẹn.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</body>
</html>
