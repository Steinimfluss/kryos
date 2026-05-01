package net.kryos.notification;

public class Notification {
    private final String title;
    private final String text;
    private final NotificationType type;
    private final long createdAt;

    public Notification(String title, String text, NotificationType type) {
        this.title = title;
        this.text = text;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public NotificationType getType() {
        return type;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}