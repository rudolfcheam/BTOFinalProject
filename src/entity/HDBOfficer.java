package entity;

import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {
    private List<Project> assignedProjects = new ArrayList<>();
    private Project requestedProject;

    public HDBOfficer(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public List<Project> getAssignedProject() { return assignedProjects; }
    public void addAssignedProject(Project assignedProject) { assignedProjects.add(assignedProject); }

    public Project getRequestedProject() { return requestedProject; }
    public void setRequestedProject(Project requestedProject) { this.requestedProject = requestedProject; }
}
