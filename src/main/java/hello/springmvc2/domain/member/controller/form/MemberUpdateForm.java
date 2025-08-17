package hello.springmvc2.domain.member.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateForm {

	@NotNull(message = "{member.id.notNull}")
	private Long id;
	
	@NotBlank(message = "{member.loginId.notBlank}")
    private String loginId;
	
	@NotBlank(message = "{member.password.notBlank}")
	@Size(min = 8, max = 20, message = "{member.password.size}")
	private String password;
	
	@NotBlank(message = "{member.displayName.notBlank}")
	@Size(min = 2, max = 16, message = "{member.displayName.size}")
	private String displayName;
}
