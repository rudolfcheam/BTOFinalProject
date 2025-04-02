package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HDBManager extends User {
    private List<Project> allProjects = new ArrayList<>();
    private Project currentProject;

    public HDBManager(String nric, String password, String name, int age, String maritalStatus) {
        super(nric, password, name, age, maritalStatus);
    }

    public void setProject(Project project) {
        currentProject = project;
        if (!allProjects.contains(project)) {
            allProjects.add(project);
        }
    }
    public Project getProject() { return this.currentProject; }
    public List<Project> getAllProjects() {
        return allProjects;
    }
}
