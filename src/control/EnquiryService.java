package control;

import entity.User;

public interface EnquiryService {
    void handleEnquiries(User user, boolean isApplicantContext);
    
}
