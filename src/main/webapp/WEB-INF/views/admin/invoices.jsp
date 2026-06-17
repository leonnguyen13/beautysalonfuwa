<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý hóa đơn - FUWA Beauty Salon</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=20260601-3">
</head>
<body>
    <div class="admin-shell">
        <c:set var="activePage" value="invoices"/>
        <%@include file="sidebar.jsp"%>

        <main class="admin-main">
            <div class="admin-topbar">
                <div>
                    <p class="eyebrow">Quản lý</p>
                    <h1>Hóa đơn</h1>
                </div>
                <a class="btn btn-primary" href="#invoice-form">Tạo hóa đơn</a>
            </div>

            <section class="table-panel" id="invoice-form">
                <h2>Thêm hóa đơn</h2>
                <form class="form-grid" action="${pageContext.request.contextPath}/admin/invoices/save" method="post">
                    <div class="field">
                        <label>Khách hàng</label>
                        <select name="customerName" required>
                            <c:forEach var="customer" items="${customers}">
                                <option value="${customer.fullName}">${customer.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field">
                        <label>Dịch vụ</label>
                        <select name="serviceName" required>
                            <c:forEach var="service" items="${services}">
                                <option value="${service.name}">${service.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field">
                        <label>Nhân viên</label>
                        <select name="employeeName">
                            <option value="">Chọn tự động</option>
                            <c:forEach var="employee" items="${employees}">
                                <option value="${employee.fullName}">${employee.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field">
                        <label>Tổng tiền</label>
                        <input type="text" name="totalAmount" placeholder="650.000đ" required>
                    </div>
                    <div class="field">
                        <label>Hình thức thanh toán</label>
                        <select name="paymentMethod">
                            <option>Thanh toán tại salon</option>
                            <option>Chuyển khoản</option>
                            <option>Ví điện tử</option>
                        </select>
                    </div>
                    <div class="field">
                        <label>Ngày tạo</label>
                        <input type="date" name="createdDate" required>
                    </div>
                    <div class="field full">
                        <button class="btn btn-primary" type="submit">Lưu hóa đơn</button>
                    </div>
                </form>
            </section>

            <section class="table-panel">
                <h2>Danh sách hóa đơn</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Khách hàng</th>
                            <th>Dịch vụ</th>
                            <th>Nhân viên</th>
                            <th>Tổng tiền</th>
                            <th>Thanh toán</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="invoice" items="${invoices}">
                            <tr>
                                <td>${invoice.id}</td>
                                <td>${invoice.customerName}</td>
                                <td>${invoice.serviceName}</td>
                                <td>${invoice.employeeName}</td>
                                <td>${invoice.totalAmount}</td>
                                <td>${invoice.paymentMethod}</td>
                                <td>
                                    <span class="badge">
                                        <c:choose>
                                            <c:when test="${invoice.paymentStatus == 'PAID' || invoice.paymentStatus == 'Paid'}">Đã thanh toán</c:when>
                                            <c:when test="${invoice.paymentStatus == 'UNPAID' || invoice.paymentStatus == 'Unpaid'}">Chưa thanh toán</c:when>
                                            <c:when test="${invoice.paymentStatus == 'PENDING' || invoice.paymentStatus == 'Pending'}">Đang chờ</c:when>
                                            <c:otherwise>${invoice.paymentStatus}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td>${invoice.createdDate}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${invoice.paymentStatus == 'PAID' || invoice.paymentStatus == 'Paid'}">
                                            <a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/invoices/print?id=${invoice.id}">In hóa đơn</a>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/admin/invoices/pay" method="post">
                                                <input type="hidden" name="id" value="${invoice.id}">
                                                <button class="btn btn-outline" type="submit">Thanh toán</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
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
