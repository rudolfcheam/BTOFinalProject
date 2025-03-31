package utility;

import entity.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataStore {
    private static List<User> users = new ArrayList<>();
    private static List<Project> projects = new ArrayList<>();
    private static List<Application> applications = new ArrayList<>();
    private static List<Enquiry> enquiries = new ArrayList<>();

    //SAMPLE DATA
    static {
        // Sample Users
        users.add(new Applicant("S1234567A", "password", "Alice", 35, "Single"));
        users.add(new Applicant("T7654321B", "password", "Bob", 40, "Married"));
        users.add(new HDBOfficer("S1122334C", "password", "Officer Joe", 36, "Married"));
        users.add(new HDBManager("T9988776D", "password", "Manager Jane", 45, "Married"));

        // Sample Project
        HDBManager jane = (HDBManager) users.get(3);
        Project p1 = new Project("Acacia Breeze", "Yishun", jane, 2, 350000, 3, 450000);

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        String fakeDates = "15 2 2025 20 3 2025\n";
        ByteArrayInputStream fakeIn = new ByteArrayInputStream(fakeDates.getBytes());
        InputStream originalIn = System.in;
        System.setIn(fakeIn);
        p1.setDates();
        System.setOut(originalOut);
        System.setIn(originalIn);

        projects.add(p1);
    }


    public static List<User> getUsers() { return users; }
    public static List<Project> getProjects() { return projects; }
    public static List<Application> getApplications() { return applications; }
    public static List<Enquiry> getEnquiries() { return enquiries; }

    public static Application getApplicationByNric(String nric) {
        for (Application app : applications) {
            if (app.getApplicant().getNric().equalsIgnoreCase(nric)) {
                return app;
            }
        }
        return null;
    }

    public static List<Project> getProjectsByManager(HDBManager manager) {
        return projects.stream().filter(
                p -> p.getManager().equals(manager))
                .collect(Collectors.toList());
    }

    public static List<Project> getVisibleProjectsFor(Applicant applicant) {
        return projects.stream().filter(p -> {
            return (
                    p.isVisible() && (
                    (applicant.getAge() >= 35 && applicant.getMaritalStatus().equalsIgnoreCase("Single")) ||
                    (applicant.getAge() >= 21 && applicant.getMaritalStatus().equalsIgnoreCase("Married"))
                    )
            );
        }).collect(Collectors.toList());
    }
}
