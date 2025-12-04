package librarymanagement.strategies.borrowing;

import librarymanagement.enums.MemberStatus;
import librarymanagement.models.Member;
import librarymanagement.models.BookCopy;

/**
 * Rule that checks if member is in good standing (active status).
 */
public class MemberStatusRule implements BorrowingRule {
    
    @Override
    public ValidationResult validate(Member member, BookCopy bookCopy, int currentBorrowCount) {
        if (member.getStatus() != MemberStatus.ACTIVE) {
            return ValidationResult.failure(
                    "Member is not active. Current status: " + member.getStatus(),
                    getRuleName());
        }
        
        return ValidationResult.success();
    }

    @Override
    public String getRuleName() {
        return "MemberStatusRule";
    }
}



