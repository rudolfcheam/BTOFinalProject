package control;

import entity.*;
import utility.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplicationController {
    private Scanner scanner = new Scanner(System.in);

    public void applyForProject(Applicant applicant) {
        List<Project> available = DataStore.getVisibleProjectsFor(applicant);

        if (applicant.getApplication() != null) {
            System.out.println("You have already applied for a project.");
            return;
        }

        System.out.println("Available Projects:");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("%d. %s (%s)\n", i + 1, available.get(i).getName(), available.get(i).getNeighborhood());
        }

        System.out.print("Choose project: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;

        if (choice >= 0 && choice < available.size()) {
            Project chosen = available.get(choice);
            Application application = new Application(applicant, chosen);
            applicant.setApplication(application);
            applicant.addToAppHistory(application);
            DataStore.getApplications().add(application);
            System.out.println("Application submitted successfully.");
        } else {
            System.out.println("Invalid selection.");
        }
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
        System.out.print("Enter applicant NRIC: ");
        String nric = sc.nextLine().trim();

        Application app = DataStore.getApplicationByNric(nric);
        if (app == null || !app.getProject().equals(officer.getAssignedProject())) {
            System.out.println("No such application found or not under your project.");
            return;
        }

        if (!app.getStatus().equals("Successful")) {
            System.out.println("Application not eligible for booking.");
            return;
        }

        System.out.print("Enter flat type to book (2-Room/3-Room): ");
        String flatType = sc.nextLine().trim();

        if (officer.getAssignedProject().bookFlat(flatType)) {
            app.setStatus("Booked");
            app.getApplicant().setFlatType(flatType);
            System.out.println("Flat booked successfully!");
        } else {
            System.out.println("Selected flat type is no longer available.");
        }
    }

    public void viewApplications(HDBOfficer officer) {
        Project project = officer.getAssignedProject();
        if (project == null) {
            return;
        }
        System.out.println("Applications for: " + project.getName());
        for (Application app : DataStore.getApplications()) {
            if (app.getProject().equals(project)) {
                System.out.printf("- %s (%s): %s\n",
                        app.getApplicant().getName(),
                        app.getApplicant().getNric(),
                        app.getStatus());
            }
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
        System.out.printf("%d Total Applications", index);
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

        System.out.print("Enter the application to process (0 to cancel):");
        int choice = myScanner.nextInt();
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

        System.out.print("Enter the application to process (0 to cancel):");
        int choice = myScanner.nextInt();
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


}

