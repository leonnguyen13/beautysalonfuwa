package uef.edu.vn.beautysalonfuwa.model;

public class Appointment {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String service;
    private String employee;
    private String appointmentDate;
    private String appointmentTime;
    private String paymentMethod;
    private String status;
    private String note;

    public Appointment() {
    }

    public Appointment(int id, String fullName, String phone, String service, String employee,
            String appointmentDate, String appointmentTime, String paymentMethod, String status) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.service = service;
        this.employee = employee;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
