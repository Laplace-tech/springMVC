package hello.springmvc2.domain.member.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hello.springmvc2.domain.member.controller.form.MemberSaveForm;
import hello.springmvc2.domain.member.controller.form.MemberUpdateForm;
import hello.springmvc2.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberValidator implements Validator {

	private final MemberService memberService;

	@Override
	public boolean supports(Class<?> clazz) {
		return MemberSaveForm.class.isAssignableFrom(clazz) || 
			   MemberUpdateForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		if (target instanceof MemberSaveForm form) {
			if (memberService.existsByLoginId(form.getLoginId())) {
				errors.rejectValue("loginId", "member.duplicatedUsername");
			}
		} else {
			log.warn("지원되지 않는 타입으로 검증 시도 : {}", target.getClass());
			return;
		}

	}

}
