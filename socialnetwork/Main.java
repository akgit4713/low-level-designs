package socialnetwork;

import socialnetwork.enums.PostType;
import socialnetwork.enums.PrivacyLevel;
import socialnetwork.models.*;
import socialnetwork.strategies.newsfeed.EngagementBasedNewsfeedStrategy;

import java.util.List;

/**
 * Demo class showing the usage of the Social Network LLD.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║         SOCIAL NETWORK - Low Level Design Demo          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Create the social network instance
        SocialNetwork network = new SocialNetwork();

        // ==================== User Registration ====================
        System.out.println("=== 1. USER REGISTRATION ===\n");

        User alice = network.register("Alice Johnson", "alice@example.com", "Password123");
        System.out.println("Registered: " + alice);

        User bob = network.register("Bob Smith", "bob@example.com", "SecurePass456");
        System.out.println("Registered: " + bob);

        User charlie = network.register("Charlie Brown", "charlie@example.com", "Charlie789");
        System.out.println("Registered: " + charlie);

        // ==================== Authentication ====================
        System.out.println("\n=== 2. AUTHENTICATION ===\n");

        Session aliceSession = network.login("alice@example.com", "Password123");
        System.out.println("Alice logged in with token: " + aliceSession.getToken().substring(0, 8) + "...");

        User validatedAlice = network.validateSession(aliceSession.getToken());
        System.out.println("Session validated for: " + validatedAlice.getName());

        // ==================== Profile Updates ====================
        System.out.println("\n=== 3. PROFILE UPDATES ===\n");

        alice = network.updateProfile(alice.getId(), null, 
                "Software engineer passionate about building social platforms", 
                "Technology, Music, Travel");
        System.out.println("Updated Alice's bio: " + alice.getBio());

        alice = network.updateProfilePicture(alice.getId(), "https://example.com/alice-pic.jpg");
        System.out.println("Updated Alice's profile picture: " + alice.getProfilePictureUrl());

        alice = network.updatePrivacySettings(alice.getId(), PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS_ONLY);
        System.out.println("Updated Alice's privacy settings");

        // ==================== Friend Requests ====================
        System.out.println("\n=== 4. FRIEND CONNECTIONS ===\n");

        FriendRequest aliceToBob = network.sendFriendRequest(alice.getId(), bob.getId());
        System.out.println("Alice sent friend request to Bob: " + aliceToBob.getId());

        FriendRequest aliceToCharlie = network.sendFriendRequest(alice.getId(), charlie.getId());
        System.out.println("Alice sent friend request to Charlie: " + aliceToCharlie.getId());

        // Bob accepts Alice's request
        network.acceptFriendRequest(aliceToBob.getId(), bob.getId());
        System.out.println("Bob accepted Alice's friend request");

        // Charlie declines
        network.declineFriendRequest(aliceToCharlie.getId(), charlie.getId());
        System.out.println("Charlie declined Alice's friend request");

        // Check friendships
        System.out.println("\nAre Alice and Bob friends? " + network.areFriends(alice.getId(), bob.getId()));
        System.out.println("Are Alice and Charlie friends? " + network.areFriends(alice.getId(), charlie.getId()));

        // View friends list
        List<User> aliceFriends = network.getFriends(alice.getId());
        System.out.println("Alice's friends: " + aliceFriends);

        // ==================== Posts ====================
        System.out.println("\n=== 5. POSTS ===\n");

        Post alicePost1 = network.createTextPost(alice.getId(), 
                "Hello everyone! Just joined this amazing social network! 🎉");
        System.out.println("Alice created post: " + alicePost1.getContent());

        Post alicePost2 = network.createPost(alice.getId(), 
                "Check out this beautiful sunset!", 
                PostType.IMAGE, 
                "https://example.com/sunset.jpg", 
                PrivacyLevel.PUBLIC);
        System.out.println("Alice created image post: " + alicePost2.getContent());

        Post bobPost = network.createTextPost(bob.getId(), 
                "Great to connect with Alice today! Looking forward to more interactions.");
        System.out.println("Bob created post: " + bobPost.getContent());

        // ==================== Newsfeed ====================
        System.out.println("\n=== 6. NEWSFEED ===\n");

        List<Post> aliceNewsfeed = network.getNewsfeed(alice.getId(), 10);
        System.out.println("Alice's Newsfeed (" + aliceNewsfeed.size() + " posts):");
        for (Post post : aliceNewsfeed) {
            User author = network.getUser(post.getAuthorId());
            System.out.println("  - " + author.getName() + ": " + 
                    truncate(post.getContent(), 50) + 
                    " [Likes: " + post.getLikeCount() + ", Comments: " + post.getCommentCount() + "]");
        }

        // ==================== Likes & Comments ====================
        System.out.println("\n=== 7. LIKES & COMMENTS ===\n");

        // Bob likes Alice's post
        network.likePost(alicePost1.getId(), bob.getId());
        System.out.println("Bob liked Alice's first post");

        // Bob comments on Alice's post
        Comment bobComment = network.addComment(alicePost1.getId(), bob.getId(), 
                "Welcome to the network! Great to have you here!");
        System.out.println("Bob commented: " + bobComment.getContent());

        // Alice likes Bob's post
        network.likePost(bobPost.getId(), alice.getId());
        System.out.println("Alice liked Bob's post");

        // Alice comments back
        network.addComment(bobPost.getId(), alice.getId(), 
                "Thanks for the warm welcome, Bob!");
        System.out.println("Alice replied to Bob's post");

        // Check likes
        System.out.println("\nWho liked Alice's first post?");
        List<User> likers = network.getUsersWhoLiked(alicePost1.getId());
        for (User user : likers) {
            System.out.println("  - " + user.getName());
        }

        // Check comments
        System.out.println("\nComments on Alice's first post:");
        List<Comment> comments = network.getComments(alicePost1.getId());
        for (Comment comment : comments) {
            User author = network.getUser(comment.getAuthorId());
            System.out.println("  - " + author.getName() + ": " + comment.getContent());
        }

        // Updated post info
        Post updatedPost = network.getPost(alicePost1.getId(), bob.getId());
        System.out.println("\nAlice's first post now has " + 
                updatedPost.getLikeCount() + " likes and " + 
                updatedPost.getCommentCount() + " comments");

        // ==================== Notifications ====================
        System.out.println("\n=== 8. NOTIFICATIONS ===\n");

        int aliceUnread = network.getUnreadNotificationCount(alice.getId());
        System.out.println("Alice has " + aliceUnread + " unread notifications");

        List<Notification> aliceNotifications = network.getNotifications(alice.getId());
        System.out.println("Alice's notifications:");
        for (Notification notification : aliceNotifications) {
            System.out.println("  - [" + (notification.isRead() ? "READ" : "UNREAD") + "] " + 
                    notification.getMessage());
        }

        // Mark all as read
        network.markAllNotificationsAsRead(alice.getId());
        System.out.println("\nMarked all Alice's notifications as read");

        // ==================== Privacy Demonstration ====================
        System.out.println("\n=== 9. PRIVACY CONTROLS ===\n");

        // Alice creates a private post
        Post privatePost = network.createPost(alice.getId(), 
                "This is my private thought...", 
                PostType.TEXT, null, PrivacyLevel.PRIVATE);
        System.out.println("Alice created a private post");

        // Alice creates a friends-only post
        Post friendsOnlyPost = network.createPost(alice.getId(), 
                "Hey friends! Secret party at my place this weekend!", 
                PostType.TEXT, null, PrivacyLevel.FRIENDS_ONLY);
        System.out.println("Alice created a friends-only post");

        // Try to access posts as different users
        try {
            network.getPost(privatePost.getId(), bob.getId());
        } catch (Exception e) {
            System.out.println("Bob cannot view Alice's private post: " + e.getMessage());
        }

        try {
            Post viewed = network.getPost(friendsOnlyPost.getId(), bob.getId());
            System.out.println("Bob can view Alice's friends-only post: " + 
                    truncate(viewed.getContent(), 40));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ==================== Strategy Pattern Demo ====================
        System.out.println("\n=== 10. NEWSFEED STRATEGIES ===\n");

        // Switch to engagement-based newsfeed
        network.setNewsfeedStrategy(new EngagementBasedNewsfeedStrategy());
        System.out.println("Switched to Engagement-Based newsfeed strategy");

        List<Post> engagementFeed = network.getNewsfeed(alice.getId(), 5);
        System.out.println("Alice's Engagement-Based Newsfeed:");
        for (Post post : engagementFeed) {
            User author = network.getUser(post.getAuthorId());
            System.out.println("  - " + author.getName() + ": " + 
                    truncate(post.getContent(), 40) + 
                    " [Engagement: " + (post.getLikeCount() + post.getCommentCount()) + "]");
        }

        // ==================== User Search ====================
        System.out.println("\n=== 11. USER SEARCH ===\n");

        List<User> searchResults = network.searchUsers("alice");
        System.out.println("Search results for 'alice':");
        for (User user : searchResults) {
            System.out.println("  - " + user.getName() + " (" + user.getEmail() + ")");
        }

        // ==================== Logout ====================
        System.out.println("\n=== 12. LOGOUT ===\n");

        network.logout(aliceSession.getToken());
        System.out.println("Alice logged out");

        try {
            network.validateSession(aliceSession.getToken());
        } catch (Exception e) {
            System.out.println("Session validation failed after logout: " + e.getMessage());
        }

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    Demo Complete!                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}



