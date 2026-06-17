<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-2">
</head>
<body>
    <header class="site-header">
        <div class="nav-wrap">
            <a class="brand" href="${pageContext.request.contextPath}/"><span class="brand-mark">FW</span> FUWA Salon</a>
            <nav class="main-nav">
                <a class="active" href="${pageContext.request.contextPath}/">Trang chủ</a>
                <a href="${pageContext.request.contextPath}/services">Dịch vụ</a>
                <a href="${pageContext.request.contextPath}/booking">Đặt lịch</a>
                <c:if test="${not empty sessionScope.authUser}">
                    <a href="${pageContext.request.contextPath}/my-appointments">Lịch sử</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/#stylists">Nhân viên</a>
                <a href="${pageContext.request.contextPath}/#contact">Liên hệ</a>
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
        <section class="hero">
            <div class="hero-inner">
                <p class="eyebrow">Chăm sóc tóc · Spa · Làm đẹp</p>
                <h1>Salon chăm sóc tóc và sắc đẹp theo phong cách hiện đại.</h1>
                <p>FUWA giúp khách hàng đặt lịch nhanh, chọn dịch vụ phù hợp và theo dõi lịch hẹn ngay trên hệ thống.</p>
                <div class="hero-actions">
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/booking">Đặt lịch ngay</a>
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/services">Xem dịch vụ</a>
                </div>
            </div>
        </section>

        <section class="section">
            <div class="container">
                <div class="section-head">
                    <h2>Dịch vụ nổi bật</h2>
                    <p>Các gói dịch vụ được thiết kế cho nhu cầu chăm sóc tóc, da đầu và thư giãn tại salon.</p>
                </div>
                <div class="service-grid">
                    <c:forEach var="service" items="${featuredServices}">
                        <article class="service-card">
                            <img src="${service.imageUrl}" alt="${service.category}">
                            <div class="card-body">
                                <h3>${service.name}</h3>
                                <p>${service.description}</p>
                                <div class="price-row">
                                    <span>Từ ${service.priceText}</span>
                                    <a href="${pageContext.request.contextPath}/booking">Đặt lịch</a>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </div>
        </section>

        <section class="section alt" id="stylists">
            <div class="container">
                <div class="section-head">
                    <div>
                        <p class="eyebrow">Đội ngũ FUWA</p>
                        <h2>Chăm sóc tận tâm trong từng dịch vụ.</h2>
                    </div>
                    <p>Nhân viên FUWA hỗ trợ tư vấn, lựa chọn dịch vụ và chăm sóc phù hợp với nhu cầu của từng khách hàng.</p>
                </div>
                <div class="stylist-grid">
                    <c:forEach var="employee" items="${featuredEmployees}" varStatus="loop">
                        <article class="stylist-card">
                            <div class="stylist-media">
                                <c:choose>
                                    <c:when test="${loop.index == 0}">
                                        <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=900&h=700&q=85" alt="${employee.fullName}">
                                    </c:when>
                                    <c:when test="${loop.index == 1}">
                                        <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=900&h=700&q=85" alt="${employee.fullName}">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=900&h=700&q=85" alt="${employee.fullName}">
                                    </c:otherwise>
                                </c:choose>
                                <span class="stylist-number">0${loop.count}</span>
                            </div>
                            <div class="card-body">
                                <span class="stylist-label">${employee.position}</span>
                                <h3>${employee.fullName}</h3>
                                <p>${employee.specialty}</p>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </div>
        </section>

        <section class="section" id="contact">
            <div class="container booking-wrap">
                <div class="booking-image"></div>
                <div class="booking-panel">
                    <p class="eyebrow">Đặt lịch</p>
                    <div class="section-head">
                        <h2>Giữ lịch hẹn trong vài bước.</h2>
                    </div>
                    <form class="form-grid" action="${pageContext.request.contextPath}/booking" method="post">
                        <input type="hidden" name="email" value="${sessionScope.email}">
                        <input type="hidden" name="employee" value="Chọn tự động">
                        <input type="hidden" name="paymentMethod" value="Thanh toán tại salon">
                        <div class="field">
                            <label>Họ tên</label>
                            <input type="text" name="fullName" value="${sessionScope.fullName}" placeholder="Nguyen Van A" required>
                        </div>
                        <div class="field">
                            <label>Số điện thoại</label>
                            <input type="tel" name="phone" pattern="0[0-9]{9}" maxlength="10" inputmode="numeric" placeholder="0901234567" title="Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số" required>
                        </div>
                        <div class="field">
                            <label>Dịch vụ</label>
                            <select name="services" required>
                                <c:forEach var="serviceName" items="${activeServiceNames}">
                                    <option value="${serviceName}">${serviceName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="field">
                            <label>Ngày hẹn</label>
                            <input type="date" name="appointmentDate" required>
                        </div>
                        <div class="field">
                            <label>Giờ hẹn</label>
                            <input type="time" name="appointmentTime" min="08:00" max="20:00" step="1800" required>
                        </div>
                        <div class="field full">
                            <label>Ghi chú</label>
                            <textarea name="note" placeholder="Yêu cầu stylist hoặc ghi chú thêm"></textarea>
                        </div>
                        <div class="field full">
                            <button class="btn btn-primary" type="submit">Gửi yêu cầu đặt lịch</button>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    </main>

    <footer class="footer">
        <div class="container footer-grid">
            <div class="footer-brand">
                <a class="brand" href="${pageContext.request.contextPath}/"><span class="brand-mark">FW</span> FUWA Salon</a>
                <p>Không gian chăm sóc tóc và sắc đẹp hiện đại, chỉn chu trong từng trải nghiệm.</p>
            </div>
            <div class="footer-block">
                <span class="footer-label">Liên hệ</span>
                <strong>123 Nguyễn Huệ, Quận 1</strong>
                <p>Thành phố Hồ Chí Minh</p>
                <p>0901 234 567 · hello@fuwa.vn</p>
            </div>
            <div class="footer-block">
                <span class="footer-label">Thời gian làm việc</span>
                <div class="working-hours">
                    <span>Thứ Hai - Chủ Nhật</span>
                    <strong>08:00 - 20:00</strong>
                </div>
                <p>Vui lòng đặt lịch trước để được phục vụ tốt nhất.</p>
            </div>
        </div>
        <div class="container footer-bottom">
            <span>FUWA Beauty Salon</span>
            <span>Chăm sóc tóc · Spa · Làm đẹp</span>
        </div>
    </footer>
</body>
</html>
