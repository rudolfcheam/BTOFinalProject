package control;

import entity.*;
import utility.DataStore;

import java.sql.SQLOutput;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ProjectController {
    private Scanner scanner = new Scanner(System.in);

    public void viewAvailableProjects(Applicant applicant) {
        List<Project> projects = DataStore.getVisibleProjectsFor(applicant);

        if (projects.isEmpty()) {
            System.out.println("No available projects at the moment.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Project project : projects) {
            System.out.printf("1. %s (%s): %d x 2-Room (%d) | %d x 3-Room (%d) \n",
                    project.getName(),
                    project.getNeighborhood(),
                    project.getFlatCounts().get("2-Room"),
                    project.getFlatPrices().get("2-Room"),
                    project.getFlatCounts().get("3-Room"),
                    project.getFlatPrices().get("3-Room"));
            System.out.printf("   Application Date for %s: %s - %s",
                    project.getName(),
                    project.getStartDate().format(formatter),
                    project.getEndDate().format(formatter)
                    );
            System.out.println();
        }
    }

    public void viewAssignedProject(HDBOfficer officer) {
        Project project = officer.getAssignedProject();
        if (project == null) {
            System.out.println("No project assigned.");
            return;
        }
        System.out.printf("Assigned Project: %s (%s)\n", project.getName(), project.getNeighborhood());
    }

    public void manageProjects(HDBManager manager) {
        System.out.println("1. Create Project");
        System.out.println("2. Edit Project");
        System.out.println("3. Delete Project");
        System.out.println("4. Exit");
        System.out.print("Select option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                createProject(manager);
                break;
            case "2":
                editProject(manager);
                break;
            case "3":
                deleteProject(manager);
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void createProject(HDBManager manager) {
        System.out.print("Enter project name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter neighborhood: ");
        String neighborhood = scanner.nextLine().trim();
        System.out.print("Enter the number of 2-Room flats: ");
        int twoRoomCount = scanner.nextInt();
        System.out.print("Enter the price of 2-Room flats: ");
        int twoRoomPrice = scanner.nextInt();
        System.out.print("Enter the number of 3-Room flats: ");
        int threeRoomCount = scanner.nextInt();
        System.out.print("Enter the price of 3-Room flats: ");
        int threeRoomPrice = scanner.nextInt();

        Project project = new Project(name, neighborhood, manager, twoRoomCount, twoRoomPrice, threeRoomCount, threeRoomPrice);
        project.setDates();
        DataStore.getProjects().add(project);
        System.out.println("Project created successfully.");
    }


    private void editProject(HDBManager manager) {
        List<Project> projects = DataStore.getProjectsByManager(manager);
        if (projects.isEmpty()) {
            System.out.println("No projects to edit.");
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, projects.get(i).getName());
        }

        System.out.print("Select project to edit: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;

        if (choice >= 0 && choice < projects.size()) {
            Project selected = projects.get(choice);
            System.out.print("New name (leave blank to keep current): ");
            String newName = scanner.nextLine().trim();
            if (!newName.isEmpty()) selected.setName(newName);
            System.out.println("Project updated.");
        } else {
            System.out.println("Invalid selection.");
        }
    }
    private void deleteProject(HDBManager manager) {
        List<Project> projects = DataStore.getProjectsByManager(manager);
        if (projects.isEmpty()) {
            System.out.println("No projects to delete.");
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, projects.get(i).getName());
        }

        System.out.print("Select project to delete: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;

        if (choice >= 0 && choice < projects.size()) {
            Project selected = projects.remove(choice);
            DataStore.getProjects().remove(selected);
            System.out.println("Project deleted.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public void toggleVisibility(HDBManager manager) {
        List<Project> projects = DataStore.getProjectsByManager(manager);

        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            System.out.printf("%d. %s (Visible: %b)\n", i + 1, projects.get(i).getName(), projects.get(i).isVisible());
        }

        System.out.print("Select project to toggle visibility: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;

        if (choice >= 0 && choice < projects.size()) {
            Project selected = projects.get(choice);
            selected.setVisible(!selected.isVisible());
            System.out.println("Visibility toggled.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public void approveOfficers(HDBManager manager) {
        for (User user : DataStore.getUsers()) {
            if (user instanceof HDBOfficer) {
                HDBOfficer officer = (HDBOfficer) user;
                if (officer.getRequestedProject() != null &&
                        officer.getRequestedProject().getManager().equals(manager) &&
                        officer.getAssignedProject() == null) {

                    System.out.printf("Approve officer %s for project %s? (Y/N): ",
                            officer.getName(), officer.getRequestedProject().getName());
                    String input = scanner.nextLine().trim().toUpperCase();

                    if (input.equals("Y")) {
                        officer.setAssignedProject(officer.getRequestedProject());
                        officer.setRequestedProject(null);
                        System.out.println("Officer approved.");
                    } else {
                        officer.setRequestedProject(null);
                        System.out.println("Officer rejected.");
                    }
                }
            }
        }
    }

    public void generateReports(HDBManager manager) {
        System.out.println("=== Report: Booked Applicants in Your Projects ===");
        for (Application app : DataStore.getApplications()) {
            if (app.getStatus().equals("Booked") &&
                    app.getProject().getManager().equals(manager)) {

                System.out.printf("Name: %s | NRIC: %s | Age: %d | Marital: %s | Flat: %s | Project: %s\n",
                        app.getApplicant().getName(),
                        app.getApplicant().getNric(),
                        app.getApplicant().getAge(),
                        app.getApplicant().getMaritalStatus(),
                        app.getApplicant().getFlatType(),
                        app.getProject().getName());
            }
        }
    }
}

