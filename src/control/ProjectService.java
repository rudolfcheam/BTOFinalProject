package control;

import entity.*;

public interface ProjectService  {
    void viewAvailableProjects(Applicant applicant);
    void viewAssignedProject(HDBOfficer officer);
    void manageProjects(HDBManager manager);
    void changeOfficerSlots(Project project);
    void changeManager(Project project, HDBManager oldManager);
    void toggleVisibility(HDBManager manager);
    void approveOfficers(HDBManager manager);
    void viewAllProjects();
    void viewOwnProjects(HDBManager manager);
}
