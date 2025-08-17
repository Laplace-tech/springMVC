package hello.springmvc2.domain.member.controller.mapper;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import hello.springmvc2.domain.member.controller.form.MemberSaveForm;
import hello.springmvc2.domain.member.controller.form.MemberUpdateForm;
import hello.springmvc2.domain.member.dto.LoginMember;
import hello.springmvc2.domain.member.dto.MemberDto;
import hello.springmvc2.domain.member.entry.Member;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberMapper {

	private final BCryptPasswordEncoder passwordEncoder;
	
	public Member toEntity(MemberSaveForm form) {
		return Member.builder()
				.loginId(form.getLoginId())
				.encryptedPassword(passwordEncoder.encode(form.getPassword()))
				.displayName(form.getDisplayName())
				.createdAt(LocalDateTime.now())
				.lastModifiedAt(LocalDateTime.now())
				.build();
	}
	
	public Member toEntity(Member existingMember, MemberUpdateForm form) {
		return Member.builder()
				.id(existingMember.getId())
				.loginId(existingMember.getLoginId())
				.createdAt(existingMember.getCreatedAt())
				.displayName(form.getDisplayName())
				.encryptedPassword(passwordEncoder.encode(form.getPassword()))
				.lastModifiedAt(LocalDateTime.now())
				.build();
	}
	
	public MemberUpdateForm toForm(Member member) {
		return MemberUpdateForm.builder()
				.id(member.getId())
				.loginId(member.getLoginId())
				.displayName(member.getDisplayName())
				.password("") // 비밀번호는 빈 문자열로 초기화
				.build();
	}
	
	public MemberDto toViewDto(Member member) {
		return MemberDto.builder()
				.id(member.getId())
				.loginId(member.getLoginId())
				.displayName(member.getDisplayName())
				.build();
	}
	
	public LoginMember toLoginMember(Member member) {
		return LoginMember.builder()
				.id(member.getId())
				.loginId(member.getLoginId())
				.displayName(member.getDisplayName())
				.build();
	}
	
}
