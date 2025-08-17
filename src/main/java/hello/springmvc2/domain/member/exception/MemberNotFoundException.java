package hello.springmvc2.domain.member.exception;

@SuppressWarnings("serial")
public class MemberNotFoundException extends RuntimeException {
	
	public MemberNotFoundException(String message) {
		super(message);
	}
	
}
