<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý nhân viên - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-3">
</head>
<body>
    <div class="admin-shell">
        <c:set var="activePage" value="employees"/>
        <%@include file="sidebar.jsp"%>

        <main class="admin-main">
            <div class="admin-topbar">
                <div>
                    <p class="eyebrow">Quản lý</p>
                    <h1>Nhân viên</h1>
                </div>
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/employees">Xóa form</a>
            </div>

            <section class="table-panel">
                <h2>${employeeForm.id > 0 ? 'Sửa nhân viên' : 'Thêm nhân viên'}</h2>
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
                <form class="form-grid" action="${pageContext.request.contextPath}/admin/employees/save" method="post">
                    <input type="hidden" name="id" value="${employeeForm.id}">
                    <div class="field">
                        <label>Họ tên</label>
                        <input type="text" name="fullName" value="${employeeForm.fullName}" required>
                    </div>
                    <div class="field">
                        <label>Số điện thoại</label>
                        <input type="tel" name="phone" value="${employeeForm.phone}" pattern="0[0-9]{9}" maxlength="10" inputmode="numeric" placeholder="0901234567" title="Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số" required>
                    </div>
                    <div class="field">
                        <label>Email</label>
                        <input type="email" name="email" value="${employeeForm.email}" required>
                    </div>
                    <c:if test="${employeeForm.id == 0}">
                        <div class="field">
                            <label>Mật khẩu</label>
                            <input type="password" name="password" placeholder="123456">
                        </div>
                        <div class="field">
                            <label>Phân quyền</label>
                            <select name="role">
                                <option value="STAFF">Nhân viên</option>
                                <c:if test="${sessionScope.role == 'ADMIN'}">
                                    <option value="MANAGER">Quản lý salon</option>
                                    <option value="ADMIN">Quản trị viên</option>
                                </c:if>
                            </select>
                        </div>
                    </c:if>
                    <c:if test="${employeeForm.id > 0}">
                        <input type="hidden" name="role" value="STAFF">
                    </c:if>
                    <div class="field">
                        <label>Chức vụ</label>
                        <input type="text" name="position" value="${employeeForm.position}" required>
                    </div>
                    <div class="field">
                        <label>Chuyên môn</label>
                        <input type="text" name="specialty" value="${employeeForm.specialty}">
                    </div>
                    <div class="field">
                        <label>Trạng thái</label>
                        <select name="status">
                            <option value="ACTIVE" ${employeeForm.status == 'ACTIVE' ? 'selected' : ''}>Đang làm</option>
                            <option value="INACTIVE" ${employeeForm.status == 'INACTIVE' ? 'selected' : ''}>Nghỉ việc</option>
                        </select>
                    </div>
                    <div class="field full">
                        <button class="btn btn-primary" type="submit">${employeeForm.id > 0 ? 'Cập nhật nhân viên' : 'Thêm nhân viên'}</button>
                    </div>
                </form>
            </section>

            <section class="table-panel">
                <h2>Danh sách nhân viên</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ tên</th>
                            <th>Số điện thoại</th>
                            <th>Email</th>
                            <th>Chức vụ</th>
                            <th>Chuyên môn</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="employee" items="${employees}">
                            <tr>
                                <td>${employee.id}</td>
                                <td>${employee.fullName}</td>
                                <td>${employee.phone}</td>
                                <td>${employee.email}</td>
                                <td>${employee.position}</td>
                                <td>${employee.specialty}</td>
                                <td>
                                    <span class="badge">
                                        <c:choose>
                                            <c:when test="${employee.status == 'ACTIVE'}">Đang làm</c:when>
                                            <c:otherwise>Nghỉ việc</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/employees/edit?id=${employee.id}">Sửa</a> |
                                    <a href="${pageContext.request.contextPath}/admin/employees/delete?id=${employee.id}"
                                       onclick="return confirm('Ngừng hoạt động nhân viên này?');">Xóa</a>
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
