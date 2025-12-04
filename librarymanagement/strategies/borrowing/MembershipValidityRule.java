package librarymanagement.strategies.borrowing;

import librarymanagement.models.Member;
import librarymanagement.models.BookCopy;

/**
 * Rule that checks if member's membership is still valid (not expired).
 */
public class MembershipValidityRule implements BorrowingRule {
    
    @Override
    public ValidationResult validate(Member member, BookCopy bookCopy, int currentBorrowCount) {
        if (!member.isMembershipValid()) {
            return ValidationResult.failure(
                    "Membership has expired. Expiry date: " + member.getMembershipExpiryDate(),
                    getRuleName());
        }
        
        return ValidationResult.success();
    }

    @Override
    public String getRuleName() {
        return "MembershipValidityRule";
    }
}



