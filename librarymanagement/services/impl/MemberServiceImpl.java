package librarymanagement.services.impl;

import librarymanagement.enums.MemberStatus;
import librarymanagement.enums.MemberType;
import librarymanagement.exceptions.LibraryException;
import librarymanagement.exceptions.MemberNotFoundException;
import librarymanagement.models.Member;
import librarymanagement.observers.EventPublisher;
import librarymanagement.observers.LibraryEvent;
import librarymanagement.repositories.MemberRepository;
import librarymanagement.services.MemberService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of MemberService with thread-safe operations.
 */
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    private final EventPublisher eventPublisher;
    private final Object memberLock = new Object();

    public MemberServiceImpl(MemberRepository memberRepository, EventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Member registerMember(String name, String email, MemberType memberType) {
        synchronized (memberLock) {
            if (memberRepository.existsByEmail(email)) {
                throw new LibraryException("Member with email " + email + " already exists");
            }
            
            Member member = new Member(name, email, memberType);
            memberRepository.save(member);
            
            eventPublisher.publish(new LibraryEvent(
                    LibraryEvent.EventType.MEMBER_REGISTERED,
                    member.getMemberId(),
                    "New member registered: " + name));
            
            return member;
        }
    }

    @Override
    public Member updateMember(String memberId, String name, String email, String phone, String address) {
        synchronized (memberLock) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId, true));
            
            // Check if email is being changed to an existing one
            if (!member.getEmail().equalsIgnoreCase(email) && memberRepository.existsByEmail(email)) {
                throw new LibraryException("Email " + email + " is already in use");
            }
            
            member.setName(name);
            member.setEmail(email);
            member.setPhone(phone);
            member.setAddress(address);
            memberRepository.save(member);
            
            return member;
        }
    }

    @Override
    public void updateMemberStatus(String memberId, MemberStatus status) {
        synchronized (memberLock) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId, true));
            
            MemberStatus oldStatus = member.getStatus();
            member.setStatus(status);
            memberRepository.save(member);
            
            if (status == MemberStatus.SUSPENDED && oldStatus != MemberStatus.SUSPENDED) {
                eventPublisher.publish(new LibraryEvent(
                        LibraryEvent.EventType.MEMBER_SUSPENDED,
                        memberId,
                        "Member suspended: " + member.getName()));
            }
        }
    }

    @Override
    public void updateMemberType(String memberId, MemberType memberType) {
        synchronized (memberLock) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId, true));
            
            member.setMemberType(memberType);
            memberRepository.save(member);
        }
    }

    @Override
    public void renewMembership(String memberId, int years) {
        synchronized (memberLock) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId, true));
            
            LocalDate currentExpiry = member.getMembershipExpiryDate();
            LocalDate newExpiry = (currentExpiry.isBefore(LocalDate.now())) 
                    ? LocalDate.now().plusYears(years)
                    : currentExpiry.plusYears(years);
            
            member.setMembershipExpiryDate(newExpiry);
            
            // Reactivate if was expired
            if (member.getStatus() == MemberStatus.EXPIRED) {
                member.setStatus(MemberStatus.ACTIVE);
            }
            
            memberRepository.save(member);
        }
    }

    @Override
    public void deleteMember(String memberId) {
        synchronized (memberLock) {
            if (!memberRepository.existsById(memberId)) {
                throw new MemberNotFoundException(memberId, true);
            }
            memberRepository.delete(memberId);
        }
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public List<Member> findByStatus(MemberStatus status) {
        return memberRepository.findByStatus(status);
    }

    @Override
    public List<Member> searchByName(String name) {
        return memberRepository.searchByName(name);
    }

    @Override
    public boolean isMemberEligibleToBorrow(String memberId) {
        return memberRepository.findById(memberId)
                .map(member -> member.isActive() && member.isMembershipValid())
                .orElse(false);
    }
}



