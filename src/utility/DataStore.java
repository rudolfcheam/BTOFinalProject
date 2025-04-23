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
        users.add(new Applicant("T2934242C", "password", "Robert", 34, "Single"));
        users.add(new Applicant("T2304952Z", "password", "Charlie", 25, "Married"));
        users.add(new HDBOfficer("S1122334C", "password", "Officer Joe", 36, "Married"));
        users.add(new HDBOfficer("S2390293D", "password", "Officer Amy", 41, "Married"));
        users.add(new HDBOfficer("S2938742G", "password", "Officer Roy", 39, "Married"));
        users.add(new HDBManager("T9988776D", "password", "Manager Jane", 45, "Married"));
        users.add(new HDBManager("S1653293K", "password", "Manager Michael", 52, "Married"));

        // Sample Project
        HDBManager jane = (HDBManager) users.get(7);
        HDBManager michael = (HDBManager) users.get(8);
        Project p1 = new Project("Acacia Breeze", "Yishun", jane, 2, 350000, 3, 450000, 10);
        

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        String fakeDates = "15 2 2025 20 3 2025\n";
        ByteArrayInputStream fakeIn = new ByteArrayInputStream(fakeDates.getBytes());
        InputStream originalIn = System.in;
        System.setIn(fakeIn);
        p1.setDates();
        projects.add(p1);

        HDBManager Michael = (HDBManager) users.get(8);
        Project p2 = new Project("Green Valley", "Boon Lay", michael, 5, 300000, 10, 400000, 8);
        
        
        String fakeDatesP2 = "1 4 2025 1 5 2025\n";
        System.setIn(new ByteArrayInputStream(fakeDatesP2.getBytes()));
        p2.setDates();
        projects.add(p2);
        
        System.setOut(originalOut);
        System.setIn(originalIn);

        
    }


    public static List<User> getUsers() { return users; }
    public static List<Project> getProjects() { return projects; }
    public static List<Application> getApplications() { return applications; }
    public static List<Enquiry> getEnquiries() { return enquiries; }

    public static Application getApplicationByNric(String nric) {
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric)) {
                return ((Applicant) user).getApplication();
            }
        }
        return null;
    }

    public static List<Project> getProjectsByManager(HDBManager manager) {
        return projects.stream().filter(
                p -> p.getManager().equals(manager))
                .collect(Collectors.toList());
    }

    public static List<Project> getVisibleProjects() {
        return projects.stream().filter(Project::isVisible).collect(Collectors.toList());
    }

    public static List<Application> getFilteredBookedApplications(
            String maritalStatusFilter,
            String flatTypeFilter,
            Integer minAge,
            Integer maxAge,
            String projectNameFilter
    ) {
        return applications.stream()
                .filter(app -> "Booked". equalsIgnoreCase(app.getStatus()))
                .filter(app -> maritalStatusFilter == null ||
                        app.getApplicant().getMaritalStatus().equalsIgnoreCase(maritalStatusFilter))
                .filter(app -> flatTypeFilter == null ||
                        app.getApplicant().getFlatType().equalsIgnoreCase(flatTypeFilter))
                .filter(app -> (minAge == null || app.getApplicant().getAge() >= minAge) &&
                                        (maxAge == null || app.getApplicant().getAge() <= maxAge))
                .filter(app -> projectNameFilter == null ||
                        app.getProject().getName().equalsIgnoreCase(projectNameFilter))
                .toList();
    }
}
