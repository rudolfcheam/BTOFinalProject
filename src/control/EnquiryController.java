package control;

import entity.*;
import utility.DataStore;

import java.util.Scanner;

public class EnquiryController {
    private Scanner scanner = new Scanner(System.in);

    public void handleEnquiries(User user) {
        System.out.println("=== Enquiry Menu ===");
        if (user instanceof Applicant) {
            System.out.println("1. View Enquiries");
            System.out.println("2. Submit Enquiry");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
        } else {
            System.out.println("1. View & Reply to Enquiries");
        }
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        if (user instanceof Applicant) {
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
        } else {
            viewAndReply(user);
        }
    }

    private void viewEnquiries(User user) {
        for (Enquiry enquiry : DataStore.getEnquiries()) {
            if (enquiry.getUser().equals(user)) {
                System.out.printf("ID: %d | Msg: %s | Reply: %s\n", enquiry.getId(), enquiry.getMessage(),
                        enquiry.getReply() == null ? "Pending" : enquiry.getReply());
            }
        }
    }

    private void submitEnquiry(Applicant applicant) {
        System.out.print("Enter enquiry message: ");
        String msg = scanner.nextLine();
        Enquiry enquiry = new Enquiry(applicant, msg);
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
        for (Enquiry enquiry : DataStore.getEnquiries()) {
            System.out.printf("ID: %d | From: %s | Msg: %s | Reply: %s\n",
                    enquiry.getId(),
                    enquiry.getUser().getName(),
                    enquiry.getMessage(),
                    enquiry.getReply() == null ? "Pending" : enquiry.getReply());
        }

        System.out.print("Enter ID to reply (or 0 to cancel): ");
        int id = Integer.parseInt(scanner.nextLine());
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
    }
}
