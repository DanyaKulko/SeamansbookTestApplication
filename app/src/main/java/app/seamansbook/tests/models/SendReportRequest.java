package app.seamansbook.tests.models;

public class SendReportRequest {
    private String assembly;
    private String user;
    private String question;
    private String message;
    private String reason;

    public SendReportRequest(String assembly, String user, String question, String message, String reason) {
        this.assembly = assembly;
        this.user = user;
        this.question = question;
        this.message = message;
        this.reason = reason;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
