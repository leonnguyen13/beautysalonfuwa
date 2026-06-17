<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>In hóa đơn - FUWA Beauty Salon</title>
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
            <div class="admin-topbar print-actions">
                <div>
                    <p class="eyebrow">Thanh toán</p>
                    <h1>In hóa đơn</h1>
                </div>
                <div class="action-row">
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/invoices">Quay lại</a>
                    <button class="btn btn-primary" type="button" onclick="window.print()">In hóa đơn</button>
                </div>
            </div>

            <section class="invoice-paper">
                <div class="invoice-header">
                    <div>
                        <span class="brand-mark">FW</span>
                        <h2>FUWA Beauty Salon</h2>
                        <p>123 Nguyễn Huệ, Quận 1, Thành phố Hồ Chí Minh</p>
                        <p>Hotline: 0901 234 567</p>
                    </div>
                    <div class="invoice-code">
                        <p>Hóa đơn</p>
                        <strong>#${invoice.id}</strong>
                        <span>${invoice.createdDate}</span>
                    </div>
                </div>

                <div class="invoice-info-grid">
                    <div>
                        <span>Khách hàng</span>
                        <strong>${invoice.customerName}</strong>
                    </div>
                    <div>
                        <span>Nhân viên phụ trách</span>
                        <strong>${invoice.employeeName}</strong>
                    </div>
                    <div>
                        <span>Hình thức thanh toán</span>
                        <strong>${invoice.paymentMethod}</strong>
                    </div>
                    <div>
                        <span>Trạng thái</span>
                        <strong>
                            <c:choose>
                                <c:when test="${invoice.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                <c:when test="${invoice.paymentStatus == 'PENDING'}">Đang chờ</c:when>
                                <c:otherwise>${invoice.paymentStatus}</c:otherwise>
                            </c:choose>
                        </strong>
                    </div>
                </div>

                <table class="invoice-table">
                    <thead>
                        <tr>
                            <th>Dịch vụ</th>
                            <th>Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${invoice.serviceName}</td>
                            <td>${invoice.totalAmount}</td>
                        </tr>
                    </tbody>
                    <tfoot>
                        <tr>
                            <th>Tổng cộng</th>
                            <th>${invoice.totalAmount}</th>
                        </tr>
                    </tfoot>
                </table>

                <div class="invoice-footer">
                    <p>Cảm ơn quý khách đã sử dụng dịch vụ tại FUWA Beauty Salon.</p>
                    <div>
                        <span>Người lập hóa đơn</span>
                        <strong>FUWA Salon</strong>
                    </div>
                </div>
            </section>
        </main>
    </div>
</body>
</html>
