package entity;

import java.util.ArrayList;
import java.util.List;

public class Applicant extends User {
    private Application application;
    private String flatType;
    private List<Application> applicationHistory = new ArrayList<>();
    public Applicant(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public String getFlatType() { return flatType; }
    public void setFlatType(String flatType) { this.flatType = flatType; }

    public List<Application> getAppHistory() { return applicationHistory; }
    public void addToAppHistory(Application application) {
        applicationHistory.add(application);
    }
}
