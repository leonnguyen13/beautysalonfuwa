<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <main class="auth-page">
        <section class="auth-panel">
            <a class="brand" href="${pageContext.request.contextPath}/"><span class="brand-mark">FW</span> FUWA Salon</a>
            <h1>Đăng nhập</h1>
            <p>Truy cập tài khoản khách hàng, nhân viên hoặc quản trị viên.</p>
            <c:if test="${not empty successMessage}">
                <div class="alert-success">
                    <strong>${successMessage}</strong>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert-success">
                    <strong>${errorMessage}</strong>
                </div>
            </c:if>
            <form class="form-grid" action="${pageContext.request.contextPath}/login" method="post" autocomplete="off">
                <div class="field full">
                    <label>Email</label>
                    <input type="email" name="email" autocomplete="off" required>
                </div>
                <div class="field full">
                    <label>Mật khẩu</label>
                    <input type="password" name="password" autocomplete="new-password" required>
                </div>
                <div class="field full">
                    <button class="btn btn-primary" type="submit">Đăng nhập</button>
                </div>
            </form>
            <div class="auth-links">
                Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
            </div>
        </section>
    </main>
</body>
</html>
