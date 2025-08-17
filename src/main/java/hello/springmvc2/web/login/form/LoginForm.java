package hello.springmvc2.web.login.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @NotNull(모든 타입) 
 * - null 금지, 하지만 ""(빈 문자열)은 허용
 * 
 * @NotEmpty(문자열, 컬렉션)
 * - null, ""(빈 문자열), 비어있는 컬렉션 금지
 * 
 * @NotBlank(문자열) 
 * - null, ""(빈 문자열), " "(공백 문자만 있는거 금지)
 * 
 * @Positive / @Negative
 * - 양수/음수만 허용(0, 음수 불가)
 * 
 * @PositiveOrZero / @NegativeOrZero
 * - 0 이상/이하 허용
 * 
 * @Max / @Min (value)
 * - 최대/최소값 지정
 * 
 * @Range(min, max)
 * - 범위 지정
 */

@Setter
@Getter
@NoArgsConstructor
public class LoginForm {

	@NotBlank(message = "{login.form.loginId}")
	private String loginId;
	
	@NotBlank(message = "{login.form.password}")
	private String password;
}
