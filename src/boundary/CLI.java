package boundary;

import control.*;
import entity.*;

import java.util.Scanner;

/**
 * Command-Line Interface (CLI) for the BTO Management System.
 * Handles user login and displays menus based on user roles
 * (Applicant, HDB Officer, HDB Manager). Delegates tasks to
 * relevant service layers.
 * 
 * This class demonstrates Dependency Inversion by depending on
 * service interfaces rather than concrete implementations.
 * 
 * @author YourName
 * @version 1.0
 */

public class CLI {
    private Scanner scanner = new Scanner(System.in);
    private final AuthService authService;
    private final ProjectService projectService;
    private final ApplicationService appService;
    private final EnquiryService enquiryService;

    /**
     * Constructs a new CLI instance with service dependencies injected.
     * 
     * @param authService       Authentication service
     * @param projectService    Project-related service
     * @param appService        Application-related service
     * @param enquiryService    Enquiry management service
     */

    public CLI(AuthService authService, ProjectService projectService, ApplicationService appService, EnquiryService enquiryService) {
        this.authService = authService;
        this.projectService = projectService;
        this.appService = appService;
        this.enquiryService = enquiryService;
    }
    
    /**
     * Starts the main loop of the application and prompts for login.
     */
    public void start() {
        while (true) {
            System.out.println("=== Welcome to BTO Management System ===");
            User user = login();
            if (user == null) {
                System.out.println("Invalid NRIC or password");
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
            return authService.login(nric, password);
        }
    }

    private void applicantMenu(Applicant applicant) {
        System.out.println("\n=== Applicant Menu ===");
        System.out.println("1. View Available Projects");
        System.out.println("2. Change Password");
        System.out.println("3. Apply for Project");
        System.out.println("4. View Application Status");
        System.out.println("5. Enquiry Management");
        System.out.println("6. Application Withdrawal Request");
        System.out.println("7. View Application History");
        if (applicant instanceof HDBOfficer) {
            System.out.println("8. Back");
        } else {
            System.out.println("8. Logout");
        }
        while (true) {

            System.out.print("[APPLICANT_MENU] Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    projectService.viewAvailableProjects(applicant);
                    break;
                case "2":
                    handlePasswordChange(applicant);
                    break;
                case "3":
                    appService.applyForProject(applicant);
                    break;
                case "4":
                    appService.viewStatus(applicant);
                    break;
                case "5":
                    enquiryService.handleEnquiries(applicant, true);
                    break;
                case "6":
                    appService.withdrawApplication(applicant);
                    break;
                case "7":
                    appService.viewApplicationHistory(applicant);
                    break;
                case "8":
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
        System.out.println("2. Change Password");
        System.out.println("3. Project Officer Registration");
        System.out.println("4. Check Project Officer Registration Status");
        System.out.println("5. View Project Details");
        System.out.println("6. Enquiry Management");
        System.out.println("7. View Applications Under Your Project");
        System.out.println("8. Manage Bookings");
        System.out.println("9. Generate Receipt for Applicant");
        System.out.println("10. Logout");
        while (true) {
            System.out.print("[OFFICER_MENU] Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    applicantMenu(officer);
                    break;
                case "2":
                    handlePasswordChange(officer);
                    break;
                case "3":
                    appService.registerForProject(officer);
                    break;
                case "4":
                    appService.checkRegistrationStatus(officer);
                    break;
                case "5":
                    projectService.viewAssignedProject(officer);
                    break;
                case "6":
                    enquiryService.handleEnquiries(officer, false);
                    break;
                case "7":
                    appService.viewApplications(officer);
                    break;
                case "8":
                    appService.manageBooking(officer);
                    break;
                case "9":
                    appService.generateReceipt(officer);
                    break;
                case "10":
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
        System.out.println("2. Change Password");
        System.out.println("3. Toggle Project Visibility");
        System.out.println("4. Manage Applications & Withdrawals");
        System.out.println("5. Officer Registration Approvals");
        System.out.println("6. Generate Reports");
        System.out.println("7. Enquiry Management");
        System.out.println("8. View All Projects");
        System.out.println("9. View Your Projects");
        System.out.println("10. Logout");

        while (true) {
            System.out.print("[MANAGER_MENU] Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    projectService.manageProjects(manager);
                    break;
                case "2":
                    handlePasswordChange(manager); // Call this method
                    break;
                case "3":
                    projectService.toggleVisibility(manager);
                    break;
                case "4":
                    appService.manageApplicationsWithdrawals(manager);
                    break;
                case "5":
                    projectService.approveOfficers(manager);
                    break;
                case "6":
                    appService.generateReports(manager);
                    break;
                case "7":
                    enquiryService.handleEnquiries(manager, false);
                    break;
                case "8":
                    projectService.viewAllProjects();
                    break;
                case "9":
                    projectService.viewOwnProjects(manager);
                    break;
                case "10":
                    System.out.println("Logging out...\n");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    private void handlePasswordChange(User user) {
        System.out.print("Enter current password: ");
        String oldPassword = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        String result = authService.changeUserPassword(user, oldPassword, newPassword);

        switch (result) {
            case "SUCCESS":
                System.out.println("Password updated successfully!");
                break;
            case "WRONG_OLD_PASSWORD":
                System.out.println("Incorrect current password. Try again.");
                break;
            case "INVALID_NEW_PASSWORD":
                System.out.println("New password must be at least 8 characters.");
                break;
        }
    }
}

