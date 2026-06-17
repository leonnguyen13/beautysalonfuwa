<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý lịch hẹn - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-3">
</head>
<body>
    <div class="admin-shell">
        <c:set var="activePage" value="appointments"/>
        <%@include file="sidebar.jsp"%>

        <main class="admin-main">
            <div class="admin-topbar">
                <div>
                    <p class="eyebrow">Quản lý</p>
                    <h1>Lịch hẹn</h1>
                </div>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/booking">Tạo lịch hẹn</a>
            </div>

            <section class="table-panel">
                <h2>Danh sách lịch hẹn</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Khách hàng</th>
                            <th>Số điện thoại</th>
                            <th>Dịch vụ</th>
                            <th>Nhân viên</th>
                            <th>Ngày</th>
                            <th>Giờ</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${appointments}">
                            <tr>
                                <td>${appointment.id}</td>
                                <td>${appointment.fullName}</td>
                                <td>${appointment.phone}</td>
                                <td>${appointment.service}</td>
                                <td>${appointment.employee}</td>
                                <td>${appointment.appointmentDate}</td>
                                <td>${appointment.appointmentTime}</td>
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
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/appointments/status" method="post">
                                        <input type="hidden" name="id" value="${appointment.id}">
                                        <select name="status">
                                            <option value="PENDING" ${appointment.status == 'PENDING' ? 'selected' : ''}>Chờ xác nhận</option>
                                            <option value="CONFIRMED" ${appointment.status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
                                            <option value="PROCESSING" ${appointment.status == 'PROCESSING' ? 'selected' : ''}>Đang xử lý</option>
                                            <option value="COMPLETED" ${appointment.status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                            <option value="CANCELLED" ${appointment.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                        <button class="btn btn-outline" type="submit">Lưu</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</body>
</html>
