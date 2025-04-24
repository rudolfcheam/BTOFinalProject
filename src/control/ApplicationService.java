package control;

import entity.*;

public interface ApplicationService {

    void applyForProject(Applicant applicant);
    void withdrawApplication(Applicant applicant);
    void viewApplicationHistory(Applicant applicant);
    void manageBooking(HDBOfficer officer);
    void registerForProject(HDBOfficer officer);
    void checkRegistrationStatus(HDBOfficer officer);
    void viewApplications(HDBOfficer officer);
    void generateReceipt(HDBOfficer officer);
    void manageApplicationsWithdrawals(HDBManager manager);
    void processPending(HDBManager manager);
    void processWithdrawal(HDBManager manager);
    void generateReports(HDBManager manager);
    void viewStatus(Applicant applicant);
    void viewAllApplications(HDBManager manager);

}
