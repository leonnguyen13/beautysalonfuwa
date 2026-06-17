package uef.edu.vn.beautysalonfuwa.model;

public class Invoice {
    private int id;
    private int appointmentId;
    private String customerName;
    private String serviceName;
    private String employeeName;
    private String totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String createdDate;

    public Invoice() {
    }

    public Invoice(int id, String customerName, String serviceName, String employeeName,
            String totalAmount, String paymentMethod, String paymentStatus, String createdDate) {
        this(id, 0, customerName, serviceName, employeeName, totalAmount, paymentMethod, paymentStatus, createdDate);
    }

    public Invoice(int id, int appointmentId, String customerName, String serviceName, String employeeName,
            String totalAmount, String paymentMethod, String paymentStatus, String createdDate) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.customerName = customerName;
        this.serviceName = serviceName;
        this.employeeName = employeeName;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
