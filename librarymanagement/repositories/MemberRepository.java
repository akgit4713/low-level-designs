package librarymanagement.repositories;

import librarymanagement.enums.MemberStatus;
import librarymanagement.models.Member;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for member operations.
 */
public interface MemberRepository {
    
    void save(Member member);
    Optional<Member> findById(String memberId);
    Optional<Member> findByEmail(String email);
    List<Member> findAll();
    List<Member> findByStatus(MemberStatus status);
    List<Member> searchByName(String name);
    void delete(String memberId);
    boolean existsById(String memberId);
    boolean existsByEmail(String email);
}



