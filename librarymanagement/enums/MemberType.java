package librarymanagement.enums;

/**
 * Represents different types of library members with varying privileges.
 */
public enum MemberType {
    STANDARD(5, 14),        // 5 books, 14 days
    PREMIUM(10, 21),        // 10 books, 21 days
    STUDENT(3, 14),         // 3 books, 14 days
    FACULTY(15, 30),        // 15 books, 30 days
    RESEARCHER(20, 60);     // 20 books, 60 days

    private final int maxBooks;
    private final int loanDurationDays;

    MemberType(int maxBooks, int loanDurationDays) {
        this.maxBooks = maxBooks;
        this.loanDurationDays = loanDurationDays;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public int getLoanDurationDays() {
        return loanDurationDays;
    }
}



