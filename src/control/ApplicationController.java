package control;

import entity.*;
import utility.DataStore;

import java.util.*;

public class ApplicationController implements ApplicationService {
    private Scanner scanner = new Scanner(System.in);

    public int get_int_input(String s) {
        while (true) {
            try {
                System.out.print(s);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("Please enter a non-negative number.");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number");
            }
        }
    }

    public void applyForProject(Applicant applicant) {

        if (applicant.getApplication() != null) {
            System.out.printf("You have already applied for the project: %s.\n",
                    applicant.getApplication().getProject().getName());
            System.out.println("Withdraw your current application before applying for a new one.");
            return;
        }

        List<Project> available = DataStore.getVisibleProjects();

        if (applicant instanceof HDBOfficer officer) {
            available = available.stream()
                    .filter(p -> !officer.getAssignedProject().contains(p))
                    .toList();
        }

        if (available.isEmpty()) {
            System.out.println("No projects available for application.");
            return;
        }

        System.out.println("Available Projects:");
        for (int i = 0; i < available.size(); i++) {
            Project p = available.get(i);
            System.out.printf("%d. %s (%s) | 2R (%d) | 3R (%d)\n",
                    i + 1, p.getName(), p.getNeighborhood(),
                    p.getFlatCounts().get("2-Room"), p.getFlatCounts().get("3-Room"));
        }
        System.out.println("0. Cancel and return to menu");
        System.out.printf("[%d projects available]\n", available.size());

        while (true) {
            System.out.print("Choose project (0 to cancel): ");
            int choice = get_int_input("") - 1;

            if (choice == -1) {
                System.out.println("Application cancelled.");
                return;
            }

            if (choice >= 0 && choice < available.size()) {
                Project chosen = available.get(choice);
                if (validateChoice(chosen, applicant)) {

                    Application app = new Application(applicant, chosen);
                    app.setFlatTypeBooked(applicant.getFlatType());
                    applicant.setApplication(app); 
                    DataStore.getApplications().add(app);

                    System.out.println("Application submitted.");
                    return;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }
    }

    private boolean validateChoice(Project project, Applicant applicant) {
        if (applicant instanceof HDBOfficer) {
            if (((HDBOfficer) applicant).getAssignedProject().contains(project) ||
                    applicant.getApplication() != null || ((HDBOfficer) applicant).getRequestedProject().equals(project)) {
                System.out.println("HDB Officers cannot apply for projects that they are handling");
                return false;
            }
        }
        if (project.getFlatCounts().get("2-Room") == 0 && project.getFlatCounts().get("3-Room") == 0) {
            System.out.println("All flats for this project have been booked! Please choose again.");
            return false;
        }
        if (project.getFlatCounts().get("2-Room") == 0 && !applicant.eligibleFor3Room()) {
            System.out.println("You are only eligible for 2-Room flats. All 2-Room flats in this project have been booked!");
            return false;
        }

        return true;
    }

    public void withdrawApplication(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null) {
            System.out.println("You have not applied for a flat!");
            return;
        }
        if (app.getStatus().equalsIgnoreCase("Withdrawn")) {
            System.out.println("You have already withdrawn your application");
            return;
        } else if (app.getStatus().equalsIgnoreCase("WithdrawRequested")) {
            System.out.println("You have already submitted a withdrawal request.");
            return;
        }

        Scanner myScanner = new Scanner(System.in);
        System.out.print("Are you sure you want to withdraw? (Y/N): ");
        String confirm = myScanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            app.setStatus("WithdrawRequested");
            System.out.println("Your withdrawal request has been submitted for manager approval");
        } else {
            System.out.println("Request Cancelled.");
        }
    }

    public void viewStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null) {
            System.out.println("You have not applied for a flat!");
            return;
        }
        System.out.printf("Project: %s\nStatus: %s\n", app.getProject().getName(), app.getStatus());
    }

    public void viewApplicationHistory(Applicant applicant) {
        List<Application> appHistory = applicant.getAppHistory();

        if (appHistory.isEmpty()) {
            System.out.println("No applications have been made yet.");
            return;
        }
        System.out.println("=== Application History ===");
        int index = 1;
        for (Application app : appHistory) {
            System.out.printf("%d. Project: %s | Status: %s\n", index++, app.getProject().getName(), app.getStatus());
        }
    }

    public void manageBooking(HDBOfficer officer) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter NRIC of applicant whose booking you want to manage: ");
        String nric = sc.nextLine().trim();

        Application app = DataStore.getApplicationByNric(nric);
        if (app == null || !(officer.getAssignedProject().contains(app.getProject()))) {
            System.out.println("The user has either not made an application, or the project is not assigned to you");
            return;
        }

        if (!app.getStatus().equals("Successful")) {
            System.out.println("Application not eligible for booking.");
            return;
        }

        if (!app.getApplicant().eligibleFor3Room()) {
            System.out.printf("Applicant is only eligible for 2-Room flats at %s." +
                    "\nWould you like to book a 2-Room flat for the applicant? (Y/N): ", app.getProject().getName());
            String response = scanner.nextLine().trim();
            if (response.equalsIgnoreCase("Y")) {
                if (app.getProject().bookFlat("2-Room")) {
                    System.out.printf("2-Room flat in %s booked successfully for %s (%s)! Congratulations!",
                            app.getProject().getName(),
                            app.getApplicant().getName(),
                            app.getApplicant().getNric());
                    app.setStatus("Booked");
                    app.setFlatTypeBooked("2-Room");

                    app.getApplicant().setApplication(app);
                    app.getApplicant().addToAppHistory(app);
                    DataStore.getApplications().add(app);

                } else {
                    System.out.println("Flat unavailable! No vacancies left!");
                }
            } else {
                System.out.println("Operation cancelled");
            }
        } else {

            System.out.printf("Applicant is eligible for both 2-Room and 3-Room flats in %s", app.getProject().getName());
            System.out.println(""" 
                            1. 2-Room
                            2. 3-Room""");

            List<String> flatTypes = Arrays.asList("2-Room", "3-Room");
            int response;

            do {
                response = Integer.parseInt((sc.nextLine().trim()));
                if (1 <= response && response <= flatTypes.size()) {
                    System.out.printf("%s flat selected. Please wait a moment...\n", flatTypes.get(response - 1));
                    if (app.getProject().bookFlat(flatTypes.get(response - 1))) {
                        System.out.printf("%s flat in %s booked successfully for %s (%s)! Congratulations!",
                            flatTypes.get(response - 1),
                            app.getProject().getName(),
                            app.getApplicant().getName(),
                            app.getApplicant().getNric());
                        app.setStatus("Booked");
                        app.setFlatTypeBooked(flatTypes.get(response - 1));

                        app.getApplicant().setApplication(app);
                        app.getApplicant().addToAppHistory(app);
                        DataStore.getApplications().add(app);
                    } else {
                        System.out.println("Flat unavailable! No vacancies left!");
                    }
                } else {
                    System.out.println("Invalid response!");
                }
            } while (response < 0 || response > flatTypes.size());
        }
    }


    public void registerForProject(HDBOfficer officer) {
    if (officer.getRequestedProject() != null) {
        System.out.printf("You have already applied for %s. Would you like to reapply for a different project instead? (Y/N): ", officer.getRequestedProject().getName());
        String selection = scanner.nextLine().trim();
        if (!selection.equalsIgnoreCase("y")) {
            System.out.println("Application for your project still preserved.");
            return;
        }
    }

    System.out.println("These are the HDB projects that are available for registration: (MAX 10 officers per project)");

    System.out.println("=== Available Projects ===");
    List<Project> allProjects = DataStore.getProjects();

    int index = 0;
    for (Project project : allProjects) {
        int remainingSlots = project.getOfficerSlots() - project.getOfficersList().size();
        System.out.printf("%d. %s (%s) [%s to %s] (Max - %d slots | %d slots left)\n",
                ++index,
                project.getName(),
                project.getNeighborhood(),
                project.getStartDate(),
                project.getEndDate(),
                project.getOfficerSlots(),
                remainingSlots);
    }

    int choice = get_int_input("Select the project that you would like to register for: ");
    scanner.nextLine();

    if (choice < 1 || choice > allProjects.size()) {
        System.out.println("Invalid Project.");
    } else {
        Project selected = allProjects.get(choice - 1);
        
        
        if (selected.getOfficerSlots() - selected.getOfficersList().size() <= 0) {
            System.out.println("No available slots left for this project.");
            return;
        }

        boolean validChoice = validRegistration(officer, selected);
        if (validChoice) {
            officer.setRequestedProject(selected);
            selected.addOfficer(officer); 
            System.out.println("Request submitted for manager's approval.");
        } else {
            System.out.println("You cannot apply for this project!");
        }
    }
}


    //Checks if the project selected by the officer was applied by him, or if it overlaps with other projects that he has applied for
    private boolean validRegistration(HDBOfficer officer, Project project) {

        if (officer.getApplication() != null &&
                officer.getApplication().getProject().equals(project) &&
                project.getOfficerSlots() > project.getOfficersList().size()) {
            return false;
        } else if (officer.getAssignedProject() != null) {
            for (Project assignedProject : officer.getAssignedProject() ) {
                if (!(assignedProject.getStartDate().isAfter(project.getEndDate()) ||
                        assignedProject.getEndDate().isBefore(project.getStartDate()))) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }


    public void checkRegistrationStatus(HDBOfficer officer) {
    if (officer.getRequestedProject() != null) {
        System.out.printf("Your registration to join the Project %s is still pending.\n", 
            officer.getRequestedProject().getName());
    } else if (officer.getAssignedProject().isEmpty()) {
        System.out.println("You have not registered to join any new projects.");
    }

    if (!officer.getAssignedProject().isEmpty()) {
        System.out.println("You have already been successfully registered for the following projects: ");
        int index = 0;
        for (Project project : officer.getAssignedProject()) {
            System.out.printf("%d. %s [%s to %s]",
                    ++index,
                    project.getName(),
                    project.getStartDate(),
                    project.getEndDate());
        }
    }
}
    

    public void viewApplications(HDBOfficer officer) {
        List<Project> assignedProjects = officer.getAssignedProject();
        if (assignedProjects.isEmpty()) {
            System.out.println("You have not been assigned to a project.");
            return;
        }
        for (Project project: assignedProjects) {
            System.out.printf("==== Applications for: %s ====", project.getName());
            int index = 0;
            for (Application app : DataStore.getApplications()) {
                if (app.getProject().equals(project)) {
                    System.out.printf("%d. %s (%s): %s\n",
                            ++index,
                            app.getApplicant().getName(),
                            app.getApplicant().getNric(),
                            app.getStatus());
                }
            }
            if (index == 0) {
                System.out.println("There have not been any applications for your projects.");
            }
        }
    }


    public void generateReceipt(HDBOfficer officer) {
    System.out.println("Enter the NRIC number of the user that you would like to generate a receipt for: ");
    
    String nric = scanner.nextLine().trim(); 
    if (nric.isEmpty()) {
        System.out.println("Error: NRIC cannot be empty. Please try again.");
        return;
    }

    System.out.println("Current applications in DataStore:");
    for (Application app : DataStore.getApplications()) {
        System.out.println("NRIC: " + app.getApplicant().getNric() + ", Status: " + app.getStatus());
    }

    System.out.println("Searching for NRIC: " + nric);
    Application app = DataStore.getApplicationByNric(nric);

    if (app != null) {
        if (!app.getStatus().equalsIgnoreCase("booked")) {
            System.out.println("User has not booked a flat.");
        } else {
            System.out.printf("==== Receipt for %s ====\n", app.getApplicant().getName());
            System.out.printf("""
                    Applicant Name: %s
                    NRIC: %s
                    Age: %d
                    Marital Status: %s
                    Project Name: %s
                    Neighbourhood: %s
                    Flat Type Selected: %s
                    Flat Price: %s
                    Flat booked by: %s
                    """, app.getApplicant().getName(), app.getApplicant().getNric(), app.getApplicant().getAge(),
                    app.getApplicant().getMaritalStatus(), app.getProject().getName(), app.getProject().getNeighborhood(),
                    app.getFlatTypeBooked(), app.getProject().getFlatPrices().get(app.getFlatTypeBooked()), officer.getName());
        }
    } else {
        System.out.println("NRIC not found.");
    }
}

    public void manageApplicationsWithdrawals(HDBManager manager) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n===== Manage Applications =====");
        System.out.println("""
                    1. View All Applications
                    2. Process Pending Applications
                    3. Process Withdrawal Requests
                    4. Exit""");
        while (true) {
            System.out.print("Select Option:");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    viewAllApplications(manager);
                    break;
                case "2":
                    processPending(manager);
                    break;
                case "3":
                    processWithdrawal(manager);
                    break;
                case "4":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void viewAllApplications(HDBManager manager) {
        System.out.println("=== All Applications Under Your Projects ===");

        List<Application> apps = DataStore.getApplications().stream().filter(app->
                app.getProject().getManager().equals(manager))
                .toList();

        if (apps.isEmpty()) {
            System.out.println("You have no applications under your projects");
            return;
        }
        int index = 0;
        for (Application app : apps) {
            System.out.printf("%d. %s (%s) | Status: %s | Project: %s\n",
              ++index, app.getApplicant().getName(),
                    app.getApplicant().getNric(),
                    app.getStatus(),
                    app.getProject().getName()
            );
        }
        System.out.printf("%d Total Applications\n", index);
    }

    public void processPending(HDBManager manager) {
        Scanner myScanner = new Scanner(System.in);
        System.out.println("=== All Pending Applications Under Your Projects ===");

        List<Application> apps = DataStore.getApplications().stream().filter(app->
                app.getProject().getManager().equals(manager) &&
                app.getStatus().equalsIgnoreCase("Pending"))
                .toList();

        if (apps.isEmpty()) {
            System.out.println("There are no pending applications for your projects");
            return;
        }
        int index = 0;
        for (Application app : apps) {
            System.out.printf("%d. %s (%s) | Status: %s | Project: %s\n",
                    ++index, app.getApplicant().getName(),
                    app.getApplicant().getNric(),
                    app.getStatus(),
                    app.getProject().getName()
            );
        }

        int choice = get_int_input("Enter the application to process (0 to cancel): ");
        myScanner.nextLine();        //Get rid of newline character
        if (choice == 0 || choice > index) {
            System.out.print("Request Cancelled.");
            return;
        }

        Application selectedApplication = apps.get(choice - 1);

        System.out.println("Approve (A) or Reject (R) Application?");
        String action = myScanner.nextLine().trim().toUpperCase();

        switch (action) {
            case "A":
                selectedApplication.setStatus("Successful");
                System.out.println("Application approved.");
                break;
            case "R":
                selectedApplication.setStatus("Unsuccessful");
                System.out.println("Application rejected.");
                break;
            default:
                System.out.println("Invalid input.");
        }
    }

    public void processWithdrawal(HDBManager manager) {
        Scanner myScanner = new Scanner(System.in);
        System.out.println("=== All Withdrawal Requests Under Your Projects ===");

        List<Application> apps = DataStore.getApplications().stream().filter(app->
                        app.getProject().getManager().equals(manager) &&
                                app.getStatus().equalsIgnoreCase("WithdrawRequested"))
                .toList();

        if (apps.isEmpty()) {
            System.out.println("There are no withdrawal requests under your projects");
            return;
        }

        int index = 0;
        for (Application app : apps) {
            System.out.printf("%d. %s (%s) | Status: %s | Project: %s\n",
                    ++index, app.getApplicant().getName(),
                    app.getApplicant().getNric(),
                    app.getStatus(),
                    app.getProject().getName()
            );
        }

        int choice = get_int_input("Enter the application to process (0 to cancel): ");
        myScanner.nextLine();                   //Clear the newline before next input
        if (choice == 0 || choice > index) {
            System.out.print("Request Cancelled.");
            return;
        }

        Application selectedApplication = apps.get(choice - 1);

        System.out.println("Approve (A) or Reject (R) Withdrawal Request?");
        String action = myScanner.nextLine().trim().toUpperCase();

        switch (action) {
            case "A":
                selectedApplication.setStatus("Withdrawn");
                selectedApplication.getApplicant().setApplication(null);
                System.out.println("Withdrawal approved.");
                break;
            case "R":
                selectedApplication.restoreOldStatus();
                System.out.println("Withdrawal rejected.");
                break;
            default:
                System.out.println("Invalid input.");
        }

    }

    public void generateReports(HDBManager manager) {

        String maritalStatusFilter = null;
        String flatTypeFilter = null;
        Integer minAge = null;
        Integer maxAge = null;
        String projectNameFilter = null;

        while (true) {
            System.out.println("\n=== Booked Applicants Report Filter Menu ===");
            System.out.println("Current Filters:");
            System.out.println("1. Marital Status: " + (maritalStatusFilter == null ? "Any" : maritalStatusFilter));
            System.out.println("2. Flat Type: " + (flatTypeFilter == null ? "Any" : flatTypeFilter));
            System.out.println("3. Min. Age: " + (minAge == null ? "Any" : minAge));
            System.out.println("4. Max. Age: " + (maxAge == null ? "Any" : maxAge));
            System.out.println("5. Project Name: " + (projectNameFilter == null ? "Any" : projectNameFilter));
            System.out.println("6. Generate Report");
            System.out.println("7. Clear all Filters");
            System.out.println("8. Back\n");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter marital status (leave blank to remove filter): ");
                    String ms = scanner.nextLine().trim();
                    maritalStatusFilter = ms.isEmpty() ? null : ms;
                    break;

                case "2":
                    System.out.print("Enter Flat Type (leave blank to remove filter): ");
                    String ft = scanner.nextLine().trim();
                    flatTypeFilter = ft.isEmpty() ? null : ft;
                    break;

                case "3":
                    System.out.print("Enter Minimum Age (leave blank to remove filter): ");
                    String minimum = scanner.nextLine().trim();
                    minAge = minimum.isEmpty() ? null : Integer.parseInt(minimum);
                    break;

                case "4":
                    System.out.print("Enter Maximum Age (leave blank to remove filter): ");
                    String maximum = scanner.nextLine().trim();
                    maxAge = maximum.isEmpty() ? null : Integer.parseInt(maximum);
                    break;

                case "5":
                    System.out.print("Enter Project Name (or leave blank to remove filter): ");
                    String projName = scanner.nextLine().trim();
                    projectNameFilter = projName.isEmpty() ? null : projName;
                    break;

                case "6":
                    List<Application> results = DataStore.getFilteredBookedApplications(
                            maritalStatusFilter, flatTypeFilter, minAge, maxAge, projectNameFilter
                    );

                    System.out.println("\n=== Booked Applicants Report ===");
                    if (results.isEmpty()) {
                        System.out.println("No matching applicants found.");
                    } else {
                        for (Application app : results) {
                            Applicant applicant = app.getApplicant();
                            Project project = app.getProject();
                            System.out.printf("Name: %s | Age: %d | Marital Status: %s | Flat Type: %s | Project: %s\n",
                                    applicant.getName(), applicant.getAge(), applicant.getMaritalStatus(), app.getFlatTypeBooked(), project.getName());
                        }
                    }
                break;

                case "7":
                    maritalStatusFilter = null;
                    flatTypeFilter = null;
                    minAge = null;
                    maxAge = null;
                    projectNameFilter = null;
                    System.out.println("All filters cleared.");
                    break;

                case "8":
                    return;

                default:
                    System.out.println("Invalid Choice.");
            }
        }
    }

}

