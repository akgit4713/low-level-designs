package librarymanagement.repositories.impl;

import librarymanagement.enums.MemberStatus;
import librarymanagement.models.Member;
import librarymanagement.repositories.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of MemberRepository.
 */
public class InMemoryMemberRepository implements MemberRepository {
    
    private final ConcurrentHashMap<String, Member> members = new ConcurrentHashMap<>();

    @Override
    public void save(Member member) {
        members.put(member.getMemberId(), member);
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return members.values().stream()
                .filter(member -> member.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(members.values());
    }

    @Override
    public List<Member> findByStatus(MemberStatus status) {
        return members.values().stream()
                .filter(member -> member.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> searchByName(String name) {
        String lowerName = name.toLowerCase();
        return members.values().stream()
                .filter(member -> member.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String memberId) {
        members.remove(memberId);
    }

    @Override
    public boolean existsById(String memberId) {
        return members.containsKey(memberId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return members.values().stream()
                .anyMatch(member -> member.getEmail().equalsIgnoreCase(email));
    }
}



