package librarymanagement.models;

import librarymanagement.enums.MemberStatus;
import librarymanagement.enums.MemberType;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a library member.
 */
public class Member {
    private final String memberId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private MemberType memberType;
    private MemberStatus status;
    private final LocalDate joinDate;
    private LocalDate membershipExpiryDate;

    public Member(String name, String email, MemberType memberType) {
        this.memberId = "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.memberType = Objects.requireNonNull(memberType, "Member type cannot be null");
        this.status = MemberStatus.ACTIVE;
        this.joinDate = LocalDate.now();
        this.membershipExpiryDate = joinDate.plusYears(1);
    }

    public Member(String memberId, String name, String email, MemberType memberType) {
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.memberType = Objects.requireNonNull(memberType, "Member type cannot be null");
        this.status = MemberStatus.ACTIVE;
        this.joinDate = LocalDate.now();
        this.membershipExpiryDate = joinDate.plusYears(1);
    }

    // Getters
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public LocalDate getMembershipExpiryDate() {
        return membershipExpiryDate;
    }

    public int getMaxBooksAllowed() {
        return memberType.getMaxBooks();
    }

    public int getLoanDurationDays() {
        return memberType.getLoanDurationDays();
    }

    // Setters
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = Objects.requireNonNull(memberType, "Member type cannot be null");
    }

    public void setStatus(MemberStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public void setMembershipExpiryDate(LocalDate membershipExpiryDate) {
        this.membershipExpiryDate = membershipExpiryDate;
    }

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    public boolean isMembershipValid() {
        return membershipExpiryDate != null && !LocalDate.now().isAfter(membershipExpiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return memberId.equals(member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format("Member{id='%s', name='%s', type=%s, status=%s}", 
                memberId, name, memberType, status);
    }
}



