package boundary;

import control.AuthController;
import control.ProjectController;
import control.ApplicationController;
import control.EnquiryController;
import entity.*;

import java.util.Scanner;

public class CLI {
    private Scanner scanner = new Scanner(System.in);
    private AuthController authController = new AuthController();
    private ProjectController projectController = new ProjectController();
    private ApplicationController appController = new ApplicationController();
    private EnquiryController enquiryController = new EnquiryController();

    public void start() {
        while (true) {
            System.out.println("=== Welcome to BTO Management System ===");
            User user = login();
            if (user == null) {
                System.out.println("User not found!");
            } else {
                switch (user) {
                    case HDBManager hdbManager:
                        managerMenu(hdbManager);
                        break;
                    case HDBOfficer hdbOfficer:
                        officerMenu(hdbOfficer);
                        break;
                    case Applicant applicant:
                        applicantMenu(applicant);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private User login() {
        while (true) {
            System.out.print("Enter NRIC: ");
            String nric = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            return authController.login(nric, password);
        }
    }

    private void applicantMenu(Applicant applicant) {
        System.out.println("\n=== Applicant Menu ===");
        System.out.println("1. View Available Projects");
        System.out.println("2. Apply for Project");
        System.out.println("3. View Application Status");
        System.out.println("4. Enquiry Management");
        System.out.println("5. Application Withdrawal Request");
        System.out.println("6. View Application History");
        if (applicant instanceof HDBOfficer) {
            System.out.println("7. Back");
        } else {
            System.out.println("7. Logout");
        }
        while (true) {

            System.out.print("[APPLICANT_MENU] Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    projectController.viewAvailableProjects(applicant);
                    break;
                case "2":
                    appController.applyForProject(applicant);
                    break;
                case "3":
                    appController.viewStatus(applicant);
                    break;
                case "4":
                    enquiryController.handleEnquiries(applicant);
                    break;
                case "5":
                    appController.withdrawApplication(applicant);
                    break;
                case "6":
                    appController.viewApplicationHistory(applicant);
                    break;
                case "7":
                    System.out.println("Logging out of Applicant Menu...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void officerMenu(HDBOfficer officer) {
        System.out.println("\n=== HDB Officer Menu ===");
        System.out.println("1. Apply as Normal Applicant");
        System.out.println("2. Project Officer Registration");
        System.out.println("3. Check Project Officer Registration Status");
        System.out.println("4. View Project Details");
        System.out.println("5. Enquiry Management");
        System.out.println("6. View Applications Under Your Project");
        System.out.println("7. Manage Bookings");
        System.out.println("8. Generate Receipt for Applicant");
        System.out.println("9. Logout");
        while (true) {
            System.out.print("[OFFICER_MENU] Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    applicantMenu(officer);
                    break;
                case "2":
                    appController.registerForProject(officer);
                    break;
                case "3":
                    appController.checkRegistrationStatus(officer);
                    break;
                case "4":
                    projectController.viewAssignedProject(officer);
                    break;
                case "5":
                    enquiryController.handleEnquiries(officer);
                    break;
                case "6":
                    appController.viewApplications(officer);
                    break;
                case "7":
                    appController.manageBooking(officer);
                    break;
                case "8":
                    appController.generateReceipt(officer);
                    break;
                case "9":
                    System.out.printf("Logging out from %s\n", officer.getName());
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void managerMenu(HDBManager manager) {
        System.out.println("\n=== HDB Manager Menu ===");
        System.out.println("1. Create/Edit/Delete Projects");
        System.out.println("2. Toggle Project Visibility");
        System.out.println("3. Manage Applications & Withdrawals");
        System.out.println("4. Officer Registration Approvals");
        System.out.println("5. Generate Reports");
        System.out.println("6. Enquiry Management");
        System.out.println("7. View All Projects");
        System.out.println("8. View Your Projects");
        System.out.println("9. Logout");

        while (true) {
            System.out.print("[MANAGER_MENU] Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    projectController.manageProjects(manager);
                    break;
                case "2":
                    projectController.toggleVisibility(manager);
                    break;
                case "3":
                    appController.manageApplicationsWithdrawals(manager);
                    break;
                case "4":
                    projectController.approveOfficers(manager);
                    break;
                case "5":
                    appController.generateReports(manager);
                    break;
                case "6":
                    enquiryController.handleEnquiries(manager);
                    break;
                case "7":
                    projectController.viewAllProjects();
                    break;
                case "8":
                    projectController.viewOwnProjects(manager);
                    break;
                case "9":
                    System.out.println("Logging out...\n");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}

