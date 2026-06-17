<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý dịch vụ - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-3">
</head>
<body>
    <div class="admin-shell">
        <c:set var="activePage" value="services"/>
        <%@include file="sidebar.jsp"%>

        <main class="admin-main">
            <div class="admin-topbar">
                <div>
                    <p class="eyebrow">Quản lý</p>
                    <h1>Dịch vụ</h1>
                </div>
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/services">Xóa form</a>
            </div>

            <section class="table-panel">
                <h2>${serviceForm.id > 0 ? 'Sửa dịch vụ' : 'Thêm dịch vụ'}</h2>
                <form class="form-grid" action="${pageContext.request.contextPath}/admin/services/save" method="post">
                    <input type="hidden" name="id" value="${serviceForm.id}">
                    <div class="field">
                        <label>Tên dịch vụ</label>
                        <input type="text" name="name" value="${serviceForm.name}" required>
                    </div>
                    <div class="field">
                        <label>Danh mục</label>
                        <input type="text" name="category" value="${serviceForm.category}" required>
                    </div>
                    <div class="field">
                        <label>Giá</label>
                        <input type="number" name="priceText" value="${serviceForm.priceText}" min="0" step="1000" required>
                    </div>
                    <div class="field">
                        <label>Đường dẫn hình ảnh</label>
                        <input type="url" name="imageUrl" value="${serviceForm.imageUrl}" required>
                    </div>
                    <div class="field full">
                        <label>Mô tả</label>
                        <textarea name="description" required>${serviceForm.description}</textarea>
                    </div>
                    <div class="field full">
                        <button class="btn btn-primary" type="submit">${serviceForm.id > 0 ? 'Cập nhật dịch vụ' : 'Thêm dịch vụ'}</button>
                    </div>
                </form>
            </section>

            <section class="table-panel">
                <h2>Danh sách dịch vụ</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên dịch vụ</th>
                            <th>Danh mục</th>
                            <th>Giá</th>
                            <th>Mô tả</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="service" items="${services}">
                            <tr>
                                <td>${service.id}</td>
                                <td>${service.name}</td>
                                <td>${service.category}</td>
                                <td>${service.priceText}</td>
                                <td>${service.description}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/services/edit?id=${service.id}">Sửa</a> |
                                    <a href="${pageContext.request.contextPath}/admin/services/delete?id=${service.id}"
                                       onclick="return confirm('Xóa dịch vụ này?');">Xóa</a>
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
