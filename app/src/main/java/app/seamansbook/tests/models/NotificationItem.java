package app.seamansbook.tests.models;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private int id;
    private String title;
    private String message;
    private String emailType;
    private String additionalLink;
    private int showUpdateButton;
    private int viewed;
    private String timestamp;

    public NotificationItem(int id, String title, String message, String emailType, String additionalLink, int showUpdateButton, int viewed, String timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.emailType = emailType;
        this.additionalLink = additionalLink;
        this.showUpdateButton = showUpdateButton;
        this.viewed = viewed;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getEmailType() {
        return emailType;
    }

    public String getAdditionalLink() {
        return additionalLink;
    }

    public int getShowUpdateButton() {
        return showUpdateButton;
    }

    public int getViewed() {
        return viewed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        String date =  timestamp.split(" ")[0];
        String[] parts = date.split("-");
        return parts[2] + "." + parts[1] + "." + parts[0];
    }
}
