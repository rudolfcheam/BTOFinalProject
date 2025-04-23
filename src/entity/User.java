package entity;

public abstract class User {
    protected String nric;
    protected String password;
    protected String name;
    protected int age;
    protected String maritalStatus;

    public User(String nric, String password, String name, int age, String maritalStatus) {
        this.nric = nric;
        this.password = password;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public String getNric() { return nric; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMaritalStatus() { return maritalStatus; }

    public void setPassword(String password) { this.password = password; }

    public boolean eligibleForBTO() {
        return !maritalStatus.equalsIgnoreCase("single") || age >= 35;
    }

    public boolean eligibleFor3Room() {
        return eligibleForBTO() && maritalStatus.equalsIgnoreCase("married");
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, nric);
    }
    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.password.equals(oldPassword)) {
            if (isPasswordValid(newPassword)) {
                this.password = newPassword;
                return true;
            }
        }
        return false;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }
}
