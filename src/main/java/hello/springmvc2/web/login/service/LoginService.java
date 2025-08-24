package hello.springmvc2.web.login.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import hello.springmvc2.domain.member.controller.mapper.MemberMapper;
import hello.springmvc2.domain.member.dto.LoginMember;
import hello.springmvc2.domain.member.repository.MemberRepository;
import hello.springmvc2.web.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

	private final MemberMapper memberMapper;
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public LoginResult loginCheckAuthentication(String loginId, String rawPassword) {

		return memberRepository.findByLoginId(loginId).map(member -> {
			if (passwordEncoder.matches(rawPassword, member.getEncryptedPassword())) {
				log.info("로그인 성공 {}", loginId);
				return LoginResult.SUCCESS;
			} else {
				log.warn("비밀번호 불일치 {}", loginId);
				return LoginResult.PASSWORD_MISMATCH;
			}
		}).orElseGet(() -> {
			log.warn("아이디 없음 {}", loginId);
			return LoginResult.USERNAME_NOT_FOUND;
		});
	}

	public void createSession(String loginId, HttpServletRequest request) {
	    LoginMember loginMember = getLoginMember(loginId);
	    HttpSession session = request.getSession();
	    session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
	}
	
	private LoginMember getLoginMember(String loginId) {
		return memberRepository.findByLoginId(loginId)
				.map(memberMapper::toLoginMember)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 LoginId : " + loginId));
	}
	
}
