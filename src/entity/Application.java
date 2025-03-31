package entity;

import java.util.Arrays;
import java.util.List;

public class Application {
    private Applicant applicant;
    private Project project;
    private List<String> allStatus = Arrays.asList("Pending", "Successful", "Unsuccessful", "Booked", "Withdrawn", "WithdrawRequested");
    private String status;
    private String statusBeforeWithdrawal;

    public Application(Applicant applicant, Project project) {
        this.applicant = applicant;
        this.project = project;
        this.status = allStatus.getFirst();
    }

    public Applicant getApplicant() { return applicant; }
    public Project getProject() { return project; }
    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status.equalsIgnoreCase("WithdrawRequested")) {
            saveOldStatus();
        }
        this.status = status;
    }
    private void saveOldStatus() {statusBeforeWithdrawal = status;}
    public void restoreOldStatus() {status = statusBeforeWithdrawal;}
}
