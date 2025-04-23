package control;

import entity.*;
import utility.DataStore;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class EnquiryController {
    private Scanner scanner = new Scanner(System.in);
    ProjectController pc = new ProjectController();

    public void handleEnquiries(User user) {
    System.out.println("=== Enquiry Menu ===");
    if (user instanceof HDBOfficer || user instanceof HDBManager) {
        viewAndReply(user);
    } else if (user instanceof Applicant) {
        
        System.out.println("1. View Enquiries");
        System.out.println("2. Submit Enquiry");
        System.out.println("3. Edit Enquiry");
        System.out.println("4. Delete Enquiry");

        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                viewEnquiries(user);
                break;
            case "2":
                submitEnquiry((Applicant) user);
                break;
            case "3":
                editEnquiry((Applicant) user);
                break;
            case "4":
                deleteEnquiry((Applicant) user);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
}

    private void viewEnquiries(User user) {
        List<Enquiry> userEnquiries = DataStore.getEnquiries().stream().filter(
                enquiry -> enquiry.getUser().equals(user)
                    ).toList();
        for (Enquiry enquiry : DataStore.getEnquiries()) {
            if (enquiry.getUser().equals(user)) {
                System.out.printf("ID: %d | Msg: %s | Project: %s | Reply: %s\n", enquiry.getId(), enquiry.getMessage(),
                        enquiry.getProject() == null ? "General Enquiry" : enquiry.getProject().getName(),
                        enquiry.getReply() == null ? "Pending" : enquiry.getReply());
            }
        }
    }


    private void submitEnquiry(Applicant applicant) {
        System.out.print("Enter enquiry message: ");
        String msg = scanner.nextLine();

        List<Project> visibleProjects = DataStore.getVisibleProjects();
        int index = 0;
        for (Project project : visibleProjects) {
            System.out.printf("%d. %s", ++index, project.getName());
        }
        System.out.printf("%d. General Enquiry", ++index);

        int choice;
        do {
            System.out.println("Select the project that you would like to enquire about: ");
            choice = scanner.nextInt();
        } while (choice < 0 || choice > index);
        Enquiry enquiry = new Enquiry(applicant, msg);
        if (choice != index) {
            enquiry.setProject(visibleProjects.get(choice - 1));
        }
        DataStore.getEnquiries().add(enquiry);
        System.out.println("Enquiry submitted.");
    }

    private void editEnquiry(Applicant applicant) {
        System.out.print("Enter enquiry ID to edit: ");
        int id = Integer.parseInt(scanner.nextLine());

        for (Enquiry enquiry : DataStore.getEnquiries()) {
            if (enquiry.getId() == id && enquiry.getUser().equals(applicant)) {
                System.out.print("Enter new message: ");
                enquiry.setMessage(scanner.nextLine());
                System.out.println("Updated.");
                return;
            }
        }
        System.out.println("Enquiry not found.");
    }

    private void deleteEnquiry(Applicant applicant) {
        System.out.print("Enter enquiry ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        Enquiry toRemove = null;
        for (Enquiry enquiry : DataStore.getEnquiries()) {
            if (enquiry.getId() == id && enquiry.getUser().equals(applicant)) {
                toRemove = enquiry;
                break;
            }
        }

        if (toRemove != null) {
            DataStore.getEnquiries().remove(toRemove);
            System.out.println("Enquiry deleted.");
        } else {
            System.out.println("Enquiry not found.");
        }
    }

    private void viewAndReply(User officerOrManager) {
    List<Enquiry> relevantEnquiries = DataStore.getEnquiries().stream().filter(
        enquiry -> {
            if (officerOrManager instanceof HDBOfficer officer) {
                // Officers see enquiries for their assigned projects or general enquiries
                return enquiry.getProject() == null || 
                    officer.getAssignedProject().contains(enquiry.getProject());
            } else if (officerOrManager instanceof HDBManager) {
                // Managers see all enquiries
                return true;
            }
            return false;
        }
    ).toList();


        for (Enquiry enquiry : relevantEnquiries) {
            System.out.printf("ID: %d | From: %s | Msg: %s | Project: %s | Reply: %s\n",
                    enquiry.getId(),
                    enquiry.getUser().getName(),
                    enquiry.getMessage(),
                    enquiry.getProject() == null ? "General Enquiry" : enquiry.getProject().getName(),
                    enquiry.getReply() == null ? "Pending" : enquiry.getReply());
        }

        System.out.print("Enter ID to reply (or 0 to cancel): ");
    String input = scanner.nextLine().trim();

    
    if (input.isEmpty()) {
        System.out.println("No input provided. Exiting...");
        return;
    }

    try {
        int id = Integer.parseInt(input);
        if (id == 0) return;

        for (Enquiry enquiry : DataStore.getEnquiries()) {
            if (enquiry.getId() == id) {
                System.out.print("Enter your reply: ");
                enquiry.setReply(scanner.nextLine());
                System.out.println("Reply submitted.");
                return;
            }
        }
        System.out.println("Invalid ID.");
    } catch (NumberFormatException e) {
        System.out.println("Invalid input! Please enter a number.");
    }
}
}
