import boundary.CLI;

public class Main {
    public static void main(String[] args) {
        ApplicationService appService = new ApplicationController();
        AuthService authService = new AuthController();
        EnquiryService enquiryService = new EnquiryController();
        ProjectService projectService = new ProjectController();

        CLI cli = new CLI(authService, projectService, appService, enquiryService);
        cli.start();
    }
}
