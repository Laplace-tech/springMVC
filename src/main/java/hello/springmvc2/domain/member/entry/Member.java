package hello.springmvc2.domain.member.entry;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class Member {
    private final Long id;
    private final String loginId;               // 로그인 아이디 (unique)
    private final String encryptedPassword;    // 암호화된 비밀번호
    private final String displayName;           // 사용자 이름 (표시명)
    private final LocalDateTime createdAt;      // 가입 일시
    private final LocalDateTime lastModifiedAt; // 최근 수정 일시
}

