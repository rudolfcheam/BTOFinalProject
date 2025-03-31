package entity;

public class HDBOfficer extends Applicant {
    private Project assignedProject;
    private Project requestedProject;

    public HDBOfficer(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public Project getAssignedProject() { return assignedProject; }
    public void setAssignedProject(Project assignedProject) { this.assignedProject = assignedProject; }

    public Project getRequestedProject() { return requestedProject; }
    public void setRequestedProject(Project requestedProject) { this.requestedProject = requestedProject; }
}
