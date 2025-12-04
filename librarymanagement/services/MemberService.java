package librarymanagement.services;

import librarymanagement.enums.MemberStatus;
import librarymanagement.enums.MemberType;
import librarymanagement.models.Member;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for member management operations.
 */
public interface MemberService {
    
    Member registerMember(String name, String email, MemberType memberType);
    Member updateMember(String memberId, String name, String email, String phone, String address);
    void updateMemberStatus(String memberId, MemberStatus status);
    void updateMemberType(String memberId, MemberType memberType);
    void renewMembership(String memberId, int years);
    void deleteMember(String memberId);
    
    Optional<Member> findById(String memberId);
    Optional<Member> findByEmail(String email);
    List<Member> getAllMembers();
    List<Member> findByStatus(MemberStatus status);
    List<Member> searchByName(String name);
    
    boolean isMemberEligibleToBorrow(String memberId);
}



