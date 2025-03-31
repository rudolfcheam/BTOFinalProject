package entity;

public class Enquiry {
    private static int counter = 1;
    private int id;
    private User user;
    private String message;
    private String reply;

    public Enquiry(User user, String message) {
        this.user = user;
        this.message = message;
        this.id = counter++;
    }

    public int getId() { return id; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
    public String getReply() { return reply; }

    public void setMessage(String message) { this.message = message; }
    public void setReply(String reply) { this.reply = reply; }
}
