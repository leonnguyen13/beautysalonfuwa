<%@page pageEncoding="UTF-8"%>
<aside class="sidebar">
    <a class="brand" href="${pageContext.request.contextPath}/"><span class="brand-mark">FW</span> FUWA</a>
    <nav class="admin-nav">
        <c:if test="${sessionScope.role == 'ADMIN'}">
            <a class="${activePage == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
            <a class="${activePage == 'services' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/services">Dịch vụ</a>
            <a class="${activePage == 'customers' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/customers">Khách hàng</a>
            <a class="${activePage == 'employees' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/employees">Nhân viên</a>
            <a class="${activePage == 'appointments' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/appointments">Lịch hẹn</a>
            <a class="${activePage == 'invoices' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/invoices">Hóa đơn</a>
            <a class="${activePage == 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reports">Báo cáo</a>
        </c:if>

        <c:if test="${sessionScope.role == 'MANAGER'}">
            <a class="${activePage == 'services' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/services">Dịch vụ</a>
            <a class="${activePage == 'employees' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/employees">Nhân viên</a>
            <a class="${activePage == 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reports">Báo cáo</a>
        </c:if>

        <c:if test="${sessionScope.role == 'STAFF'}">
            <a class="${activePage == 'appointments' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/appointments">Lịch hẹn</a>
            <a class="${activePage == 'invoices' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/invoices">Hóa đơn</a>
        </c:if>
    </nav>
</aside>
