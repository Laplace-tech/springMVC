package hello.springmvc2.domain.member.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;
import hello.springmvc2.domain.member.entry.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

	private final static Map<Long, Member> store = new ConcurrentHashMap<>();
	private final static AtomicLong sequence = new AtomicLong(0L);

    @Override
    public Member save(Member member) {
        long memberId = sequence.incrementAndGet();
        member = setId(member, memberId);
        store.put(memberId, member);
        log.info("Member saved: {}", member);
        return member;
    }

	@Override
	public Optional<Member> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}
	
	@Override
	public Optional<Member> findByLoginId(String loginId) {
		return store.values().stream()
				.filter(member -> member.getLoginId().equals(loginId))
				.findFirst();
	}
	@Override
	public List<Member> findAll() {
		return List.copyOf(store.values());
	}
	@Override
	public boolean update(Member member) {
		Long id = member.getId();
        if (!store.containsKey(id)) {
            log.warn("Member update failed. id={} not found", id);
            return false;
        }
        store.put(id, member);
        log.info("Member updated: {}", member);
        return true;
	}
	
	@Override
	public boolean delete(Long id) {
		boolean removed = store.remove(id) != null;
        if (removed) {
            log.info("Member deleted: id={}", id);
        } else {
            log.warn("Member delete failed. id={} not found", id);
        }
        return removed;
	}
	
	private Member setId(Member member, long sequenceId) {
		return member.toBuilder()
					.id(sequenceId)
					.build();
	}
	
	
}
