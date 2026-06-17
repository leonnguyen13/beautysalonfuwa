<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <main class="auth-page">
        <section class="auth-panel">
            <a class="brand" href="${pageContext.request.contextPath}/"><span class="brand-mark">FW</span> FUWA Salon</a>
            <h1>Tạo tài khoản</h1>
            <p>Đăng ký để đặt lịch và theo dõi lịch sử dịch vụ.</p>
            <c:if test="${not empty errorMessage}">
                <div class="alert-success">
                    <strong>${errorMessage}</strong>
                </div>
            </c:if>
            <form class="form-grid" action="${pageContext.request.contextPath}/register" method="post">
                <div class="field full">
                    <label>Họ tên</label>
                    <input type="text" name="fullName" value="${fullName}" required>
                </div>
                <div class="field full">
                    <label>Email</label>
                    <input type="email" name="email" value="${email}" pattern="[A-Za-z0-9._%+\-]+@fuwa\.vn" placeholder="tenkhach@fuwa.vn" title="Email phải có đuôi @fuwa.vn" required>
                </div>
                <div class="field full">
                    <label>Số điện thoại</label>
                    <input type="tel" name="phone" value="${phone}" pattern="0[0-9]{9}" maxlength="10" inputmode="numeric" placeholder="0901234567" title="Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số" required>
                </div>
                <div class="field full">
                    <label>Mật khẩu</label>
                    <input type="password" name="password" pattern="[0-9]{6}" minlength="6" maxlength="6" inputmode="numeric" placeholder="6 chữ số" title="Mật khẩu phải gồm đúng 6 chữ số" required>
                </div>
                <div class="field full">
                    <button class="btn btn-primary" type="submit">Đăng ký</button>
                </div>
            </form>
            <div class="auth-links">
                Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </div>
        </section>
    </main>
</body>
</html>
