<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lịch - FUWA Beauty Salon</title>
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
                <a class="active" href="${pageContext.request.contextPath}/booking">Đặt lịch</a>
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
            </div>
        </div>
    </header>

    <main>
        <section class="section">
            <div class="container booking-wrap">
                <div class="booking-image"></div>
                <div class="booking-panel">
                    <div class="section-head">
                        <h2>Đặt lịch hẹn</h2>
                    </div>

                    <c:if test="${not empty successMessage}">
                        <div class="alert-success">
                            <strong>${successMessage}</strong>
                            <span>
                                Dịch vụ:
                                <c:choose>
                                    <c:when test="${not empty bookedServices}">${bookedServices}</c:when>
                                    <c:otherwise>${appointment.service}</c:otherwise>
                                </c:choose>
                                · Ngày: ${appointment.appointmentDate}
                                · Giờ: ${appointment.appointmentTime}
                            </span>
                        </div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert-success">
                            <strong>${errorMessage}</strong>
                        </div>
                    </c:if>

                    <form class="form-grid" action="${pageContext.request.contextPath}/booking" method="post">
                        <div class="field">
                            <label>Họ tên</label>
                            <input type="text" name="fullName" value="${appointment.fullName}" required>
                        </div>
                        <div class="field">
                            <label>Email</label>
                            <input type="email" name="email" value="${appointment.email}">
                        </div>
                        <div class="field">
                            <label>Số điện thoại</label>
                            <input type="tel" name="phone" value="${appointment.phone}" pattern="0[0-9]{9}" maxlength="10" inputmode="numeric" placeholder="0901234567" title="Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số" required>
                        </div>
                        <div class="field">
                            <label>Nhân viên phụ trách</label>
                            <select name="employee">
                                <c:forEach var="employeeName" items="${employeeOptions}">
                                    <option value="${employeeName}" ${employeeName == appointment.employee ? 'selected' : ''}>${employeeName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="field full">
                            <label>Dịch vụ</label>
                            <div class="checkbox-list">
                                <c:forEach var="serviceName" items="${serviceOptions}">
                                    <label class="check-item">
                                        <input type="checkbox" name="services" value="${serviceName}">
                                        <span>${serviceName}</span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                        <div class="field">
                            <label>Ngày hẹn</label>
                            <input type="date" name="appointmentDate" value="${appointment.appointmentDate}" required>
                        </div>
                        <div class="field">
                            <label>Giờ hẹn</label>
                            <input type="time" name="appointmentTime" value="${appointment.appointmentTime}" min="08:00" max="20:00" step="1800" required>
                        </div>
                        <div class="field">
                            <label>Hình thức thanh toán</label>
                            <select name="paymentMethod">
                                <c:forEach var="paymentName" items="${paymentOptions}">
                                    <option value="${paymentName}" ${paymentName == appointment.paymentMethod ? 'selected' : ''}>${paymentName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="field full">
                            <label>Ghi chú</label>
                            <textarea name="note">${appointment.note}</textarea>
                        </div>
                        <div class="field full">
                            <button class="btn btn-primary" type="submit">Xác nhận đặt lịch</button>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    </main>
</body>
</html>
