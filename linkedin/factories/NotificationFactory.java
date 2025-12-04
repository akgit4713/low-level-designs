package linkedin.factories;

import linkedin.enums.NotificationType;
import linkedin.models.*;

/**
 * Factory for creating notification objects with proper content formatting.
 */
public class NotificationFactory {
    
    public static Notification createConnectionRequestNotification(User sender, User receiver) {
        String content = sender.getName() + " wants to connect with you";
        return new Notification(
                receiver.getId(),
                NotificationType.CONNECTION_REQUEST,
                content,
                sender.getId()
        );
    }
    
    public static Notification createConnectionAcceptedNotification(User accepter, User requester) {
        String content = accepter.getName() + " accepted your connection request";
        return new Notification(
                requester.getId(),
                NotificationType.CONNECTION_ACCEPTED,
                content,
                accepter.getId()
        );
    }
    
    public static Notification createMessageNotification(User sender, User receiver, Message message) {
        String content = sender.getName() + " sent you a message: " + 
                        truncate(message.getContent(), 50);
        return new Notification(
                receiver.getId(),
                NotificationType.MESSAGE_RECEIVED,
                content,
                message.getId()
        );
    }
    
    public static Notification createJobPostedNotification(User user, JobPosting job, Company company) {
        String content = company.getName() + " posted a new job: " + job.getTitle();
        return new Notification(
                user.getId(),
                NotificationType.JOB_POSTED,
                content,
                job.getId()
        );
    }
    
    public static Notification createJobApplicationNotification(User applicant, User recruiter, JobPosting job) {
        String content = applicant.getName() + " applied for " + job.getTitle();
        return new Notification(
                recruiter.getId(),
                NotificationType.JOB_APPLICATION_RECEIVED,
                content,
                job.getId()
        );
    }
    
    public static Notification createApplicationStatusNotification(User applicant, JobPosting job, String status) {
        String content = "Your application for " + job.getTitle() + " has been " + status.toLowerCase();
        return new Notification(
                applicant.getId(),
                NotificationType.JOB_APPLICATION_STATUS_UPDATE,
                content,
                job.getId()
        );
    }
    
    public static Notification createProfileViewNotification(User viewer, User profileOwner) {
        String content = viewer.getName() + " viewed your profile";
        return new Notification(
                profileOwner.getId(),
                NotificationType.PROFILE_VIEW,
                content,
                viewer.getId()
        );
    }
    
    public static Notification createEndorsementNotification(User endorser, User endorsed, Skill skill) {
        String content = endorser.getName() + " endorsed you for " + skill.getName();
        return new Notification(
                endorsed.getId(),
                NotificationType.ENDORSEMENT,
                content,
                skill.getId()
        );
    }
    
    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}



