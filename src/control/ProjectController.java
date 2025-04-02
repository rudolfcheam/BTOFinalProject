package control;

import entity.*;
import utility.DataStore;

import javax.xml.crypto.Data;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class ProjectController {
    private Scanner scanner = new Scanner(System.in);

    public void viewAvailableProjects(Applicant applicant) {
        List<Project> projects = DataStore.getVisibleProjects();

        if (DataStore.getProjects().isEmpty()) {
            System.out.println("No available projects at the moment.");
            return;
        }

        if (!applicant.eligibleForBTO()) {
            System.out.println("You are not eligible for the BTO programme!");
            return;
        }
        showProjects(applicant.eligibleFor3Room(), projects);
        System.out.println();
    }


    private void showProjects(boolean eligibleFor3Room, List<Project> projects) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int index = 1;
        if (eligibleFor3Room) {
            System.out.println("You are eligible for both 2-Room and 3-Room BTO flats!");
            for (Project project : projects) {
                System.out.printf("%d. %s (%s): %d x 2-Room (%d) | %d x 3-Room (%d) \n",
                        index++,
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
            }
        } else {
            System.out.println("You are only eligible for 2-Room Flats");
            for (Project project : projects) {
                System.out.printf("%d. %s (%s): %d x 2-Room (%d)\n",
                        index++,
                        project.getName(),
                        project.getNeighborhood(),
                        project.getFlatCounts().get("2-Room"),
                        project.getFlatPrices().get("2-Room"));
                System.out.printf("   Application Date for %s: %s - %s",
                        project.getName(),
                        project.getStartDate().format(formatter),
                        project.getEndDate().format(formatter)
                );
            }
        }
    }


    public void viewAssignedProject(HDBOfficer officer) {
        List<Project> assignedProjects = officer.getAssignedProject();
        if (assignedProjects.isEmpty()) {
            System.out.println("No projects assigned to you at the moment.");
            return;
        }
        int index = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Project project: assignedProjects) {
            System.out.printf("==== %d. %s ====\n",index++, project.getName());
            System.out.printf("""
                    Name: %s
                    Neighbourhood: %s
                    Visibility: %s
                    Manager: %s
                    2-Room Availability: %s
                    2-Room Price: %d
                    3-Room Availability: %s
                    3-Room Price: %d
                    Application Start Date: %s
                    Application Start Data: %s
                    """, project.getName(), project.getNeighborhood(), project.isVisible() ? "On" : "Off",
                                                    project.getManager().getName(), project.getFlatCounts().get("2-Room"),
                                                    project.getFlatPrices().get("2-Room"), project.getFlatCounts().get("3-Room"),
                                                    project.getFlatPrices().get("3-Room"), project.getStartDate().format(formatter),
                                                    project.getEndDate().format(formatter));
        }
        System.out.printf("You have been assigned to %d projects.\n", index - 1);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        System.out.println("Number of HDB Officer Slots: ");
        int officerSlots = scanner.nextInt();

        Project project = new Project(name, neighborhood, manager, twoRoomCount, twoRoomPrice, threeRoomCount, threeRoomPrice, officerSlots);
        project.setDates();
        DataStore.getProjects().add(project);
        System.out.println("The following project has been created:");
        System.out.printf("""
                Project Name: %s
                Neighbourhood: %s
                Number of 2-Room Flats: %d
                Price of 2-Room Flats: %d
                Number of 3-Room Flats: %d
                Price of 3-Room Flats: %d
                Application Opening Date: %s
                Application Closing Date: %s
                HDB Manager in Charge: %s
                Available HDB Officer Slots: %d
                """,
                name, neighborhood, twoRoomCount, twoRoomPrice, threeRoomCount, threeRoomPrice,
                project.getStartDate().format(formatter), project.getEndDate().format(formatter),
                manager.getName(), project.getOfficerSlots());
    }


    private void editProject(HDBManager manager) {
        List<Project> projects = DataStore.getProjectsByManager(manager);
        if (projects.isEmpty()) {
            System.out.println("You have no projects under you");
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, projects.get(i).getName());
        }

        System.out.print("Select project to edit: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;
        Project toEdit = projects.get(choice);

        System.out.println("\nIndicate what you would like to edit");
        System.out.println("""
                1. Name
                2. Neighbourhood
                3. Details of 2 Room Flats
                4. Details of 3 Room Flats
                5. Application Date
                6. Manager in Charge
                7. Available HDB Officer Slots (MAX 10)
                8. Exit""");
        choice = scanner.nextInt();
        switch(choice) {
            case 1:
                changeName(toEdit);
                break;
            case 2:
                changeNeighbourhood(toEdit);
                break;
            case 3:
                changeFlatDetails("2-room", toEdit);
                break;
            case 4:
                changeFlatDetails("3-room", toEdit);
                break;
            case 5:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate oldStart = toEdit.getStartDate();
                LocalDate oldEnd = toEdit.getEndDate();
                toEdit.setDates();
                if (!oldStart.equals(toEdit.getStartDate())) {
                    System.out.printf("Start Date updated: %s -> %s\n",
                            oldStart.format(formatter), toEdit.getStartDate().format(formatter));
                }
                if (!oldEnd.equals(toEdit.getEndDate())) {
                    System.out.printf("End Date updated: %s -> %s\n",
                            oldEnd.format(formatter), toEdit.getEndDate().format(formatter));
                }
                break;
            case 6:
                changeManager(toEdit, manager);
                break;
            case 7:
                changeOfficerSlots(toEdit);
                break;
            case 8:
                System.out.println("Exiting...");
                return;
        }
    }


    public void changeOfficerSlots(Project project) {
        System.out.println("Indicate the new number of officer slots (MAX 10)");
        int newNum = scanner.nextInt();
        System.out.printf("Number of officer slots changed: %d -> %d\n", project.getOfficerSlots(), newNum);
        project.setOfficerSlots(newNum);
    }


    public void changeManager(Project project, HDBManager oldManager) {
        System.out.println("Enter the NRIC of the new manager: ");
        String nric = scanner.nextLine().trim();
        boolean exists = false;
        for (User user: DataStore.getUsers()) {
            if (user.getNric().equalsIgnoreCase(nric)) {
                if (user instanceof HDBManager newManager) {
                    System.out.printf("Are you sure you want to transfer management to Manager %s (%s) [Y/N]:\n",
                            newManager.getName(), newManager.getNric());
                    String choice = scanner.nextLine().trim();
                    if (choice.equalsIgnoreCase("y")) {
                        project.setManager(newManager);
                        oldManager.setProject(null);
                        newManager.setProject(project);
                        System.out.printf("Management of Project %s transferred: %s -> %s\n",
                                project.getName(), oldManager.getName(), newManager.getName());
                    }
                } else {
                    System.out.println("User is not a manager!");
                }
            } else {
                System.out.println("The specified user does not exist.");
            }
        }
    }


    private void changeFlatDetails(String flatType, Project project) {
        if (flatType.equalsIgnoreCase("2-Room")) {
            System.out.println("Enter the new price of 2 Room Flats");
            int newPrice = scanner.nextInt();
            System.out.println("Enter the new quantity of 2 Room Flats");
            int newCount = scanner.nextInt();
            if (newPrice != project.getFlatPrices().get("2-Room")) {
                System.out.printf("2 Room Flat Price: $%d -> $%d\n", project.getFlatPrices().get("2-Room"), newPrice);
                project.setFlatPrices("2-Room", newPrice);
            }
            if (newCount != project.getFlatCounts().get("2-Room")) {
                System.out.printf("2 Room Flat Count: %d -> %d\n", project.getFlatCounts().get("2-Room"), newCount);
                project.setFlatCounts("2-Room", newCount);
            }
        } else {
            System.out.println("Enter the new price of 3 Room Flats: ");
            int newPrice = scanner.nextInt();
            System.out.println("Enter the new quantity of 3 Room Flats: ");
            int newCount = scanner.nextInt();
            if (newPrice != project.getFlatPrices().get("3-Room")) {
                System.out.printf("3 Room Flat Price: $%d -> $%d\n", project.getFlatPrices().get("3-Room"), newPrice);
                project.setFlatPrices("2-Room", newPrice);
            }
            if (newCount != project.getFlatCounts().get("3-Room")) {
                System.out.printf("3 Room Flat Count: %d -> %d\n", project.getFlatCounts().get("3-Room"), newCount);
                project.setFlatCounts("2-Room", newCount);
            }
        }
    }


    private void changeNeighbourhood(Project project) {
        System.out.println("Indicate the new neighbourhood name: ");
        String newName = scanner.nextLine().trim();
        System.out.printf("Project Name successfully changed from %s -> %s", project.getNeighborhood(), newName);
        project.setNeighbourhood(newName);
    }


    private void changeName(Project project) {
        System.out.println("Indicate the new project name: ");
        String newName = scanner.nextLine().trim();
        System.out.printf("Project Name successfully changed from %s -> %s", project.getName(), newName);
        project.setName(newName);
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
            System.out.printf("Visibility toggled: %b -> %b\n", !selected.isVisible(), selected.isVisible());
        } else {
            System.out.println("Invalid selection.");
        }
    }


    public void approveOfficers(HDBManager manager) {
        for (User user : DataStore.getUsers()) {
            if (user instanceof HDBOfficer officer) {

                if (officer.getRequestedProject() != null &&
                        officer.getRequestedProject().getManager().equals(manager) &&
                        officer.getAssignedProject() == null) {

                    System.out.printf("Approve officer %s for project %s? (Y/N): ",
                            officer.getName(), officer.getRequestedProject().getName());
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("Y")) {
                        officer.addAssignedProject(officer.getRequestedProject());
                        officer.setRequestedProject(null);
                        System.out.printf("Officer %s approved for Project %s.\n",
                                            officer.getName(), officer.getRequestedProject().getName());
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


    public void viewAllProjects() {
        List<Project> allProjectsByManager = DataStore.getProjects().stream()
                .sorted(Comparator.comparing(p -> p.getManager().getName()))
                .toList();

        int index = 0;
        for (Project project : allProjectsByManager) {
            System.out.printf("%d. %s | Manager: %s\n",
                    ++index, project.getName(), project.getManager().getName());
        }
    }


    public void viewOwnProjects(HDBManager manager) {
        List<Project> myProjects = DataStore.getProjects().stream()
                .filter(p -> p.getManager().equals(manager))
                .toList();
        int index = 0;
        for (Project project : myProjects) {
            System.out.println("==== My Projects ====");
            System.out.printf("%d. %s (%s)\n", ++index, project.getName(), project.getNeighborhood());
        }
    }
}

