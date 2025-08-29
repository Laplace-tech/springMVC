package hello.springmvc2.web.session;

/**
 * 세션(Session)
 * 
 * - 웹에서 서버가 사용자 상태를 관리하기 위한 공간
 * - HTTP는 기본적으로 state-less (상태 없음) 구조라, 요청마다 연결 정보가 없다.
 * - 세션을 사용하면, 로그인 상태, 장바구니, 사용자 설정 등 상태를 서버에 저장하고 요청마다 재사용 가능
 * 
 * 세션 생성 및 접근
 * 
 * - request.getSession(true) -> 세션이 없으면 새로 생성
 * - request.getSession(false) -> 세션이 없으면 null
 *   세션은 서버 메모리에 저장되고, 브라우저에는 **세션 ID(Cookie JSESSIONID)**가 전달된다
 * 
 * // 로그인 성공 시 세션에 사용자 정보 저장
 * HttpSession session = request.getSession();
 * session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
 * 
 * // 로그아웃 시 세션 삭제
 * session.invalidate(); // 세션 전체 제거
 * 
 */

public interface SessionConst {

	public static final String LOGIN_MEMBER = "loginMember";
	
}
