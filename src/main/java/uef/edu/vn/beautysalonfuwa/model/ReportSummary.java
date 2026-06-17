package uef.edu.vn.beautysalonfuwa.model;

public class ReportSummary {
    private String title;
    private String value;
    private String note;

    public ReportSummary() {
    }

    public ReportSummary(String title, String value, String note) {
        this.title = title;
        this.value = value;
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
