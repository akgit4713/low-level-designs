package socialnetwork.exceptions;

/**
 * Exception thrown when a user is not authorized to perform an action.
 */
public class UnauthorizedException extends SocialNetworkException {
    
    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException cannotViewPost() {
        return new UnauthorizedException("You don't have permission to view this post");
    }

    public static UnauthorizedException cannotModifyPost() {
        return new UnauthorizedException("You don't have permission to modify this post");
    }

    public static UnauthorizedException cannotViewProfile() {
        return new UnauthorizedException("You don't have permission to view this profile");
    }
}



