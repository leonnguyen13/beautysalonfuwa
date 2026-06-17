<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý khách hàng - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-3">
</head>
<body>
    <div class="admin-shell">
        <c:set var="activePage" value="customers"/>
        <%@include file="sidebar.jsp"%>

        <main class="admin-main">
            <div class="admin-topbar">
                <div>
                    <p class="eyebrow">Quản lý</p>
                    <h1>Khách hàng</h1>
                </div>
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/customers">Xóa form</a>
            </div>

            <section class="table-panel">
                <h2>${customerForm.id > 0 ? 'Sửa khách hàng' : 'Thêm khách hàng'}</h2>
                <c:if test="${not empty successMessage}">
                    <div class="alert-success form-alert">
                        <strong>${successMessage}</strong>
                    </div>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <div class="alert-error form-alert">
                        <strong>${errorMessage}</strong>
                    </div>
                </c:if>
                <form class="form-grid" action="${pageContext.request.contextPath}/admin/customers/save" method="post">
                    <input type="hidden" name="id" value="${customerForm.id}">
                    <div class="field">
                        <label>Họ tên</label>
                        <input type="text" name="fullName" value="${customerForm.fullName}" required>
                    </div>
                    <div class="field">
                        <label>Số điện thoại</label>
                        <input type="tel" name="phone" value="${customerForm.phone}" pattern="0[0-9]{9}" maxlength="10" inputmode="numeric" placeholder="0901234567" title="Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số" required>
                    </div>
                    <div class="field">
                        <label>Email</label>
                        <input type="email" name="email" value="${customerForm.email}">
                    </div>
                    <div class="field">
                        <label>Giới tính</label>
                        <select name="gender">
                            <option value="FEMALE" ${customerForm.gender == 'FEMALE' ? 'selected' : ''}>Nữ</option>
                            <option value="MALE" ${customerForm.gender == 'MALE' ? 'selected' : ''}>Nam</option>
                            <option value="OTHER" ${customerForm.gender == 'OTHER' ? 'selected' : ''}>Khác</option>
                        </select>
                    </div>
                    <div class="field full">
                        <label>Địa chỉ</label>
                        <input type="text" name="address" value="${customerForm.address}">
                    </div>
                    <div class="field full">
                        <button class="btn btn-primary" type="submit">${customerForm.id > 0 ? 'Cập nhật khách hàng' : 'Thêm khách hàng'}</button>
                    </div>
                </form>
            </section>

            <section class="table-panel">
                <h2>Danh sách khách hàng</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ tên</th>
                            <th>Số điện thoại</th>
                            <th>Email</th>
                            <th>Giới tính</th>
                            <th>Địa chỉ</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="customer" items="${customers}">
                            <tr>
                                <td>${customer.id}</td>
                                <td>${customer.fullName}</td>
                                <td>${customer.phone}</td>
                                <td>${customer.email}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${customer.gender == 'FEMALE'}">Nữ</c:when>
                                        <c:when test="${customer.gender == 'MALE'}">Nam</c:when>
                                        <c:otherwise>Khác</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${customer.address}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/customers/edit?id=${customer.id}">Sửa</a> |
                                    <a href="${pageContext.request.contextPath}/admin/customers/delete?id=${customer.id}"
                                       onclick="return confirm('Xóa khách hàng này nếu chưa có lịch hẹn?');">Xóa</a>
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
