package hello.springmvc2.domain.member.repository;

import java.util.List;
import java.util.Optional;

import hello.springmvc2.domain.member.entry.Member;

public interface MemberRepository {

	public Member save(Member member);
	public Optional<Member> findById(Long id);
	public Optional<Member> findByLoginId(String loginId);
	public List<Member> findAll();
	public boolean update(Member member);
	public boolean delete(Long id);
	
}
