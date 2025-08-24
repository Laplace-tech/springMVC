package hello.springmvc2.web.config;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import hello.springmvc2.domain.item.entry.Item;
import hello.springmvc2.domain.item.repository.ItemRepository;
import hello.springmvc2.domain.member.entry.Member;
import hello.springmvc2.domain.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("=== 데이터 초기화 시작 ===");
        initItems();
        initMembers();
        log.info("=== 데이터 초기화 완료 ===");
    }

    private void initItems() {
        itemRepository.save(new Item(null, "테스트 아이템1", 10_000, 10));
        itemRepository.save(new Item(null, "테스트 아이템2", 20_000, 20));
        itemRepository.save(new Item(null, "테스트 아이템3", 30_000, 30));
    }

    private void initMembers() {
        saveMember("Anna", "28482848a", "Anna");
        saveMember("Erma", "28482848a", "Erma");
    }

    private void saveMember(String loginId, String rawPassword, String displayName) {
        Member member = Member.builder()
                .loginId(loginId)
                .encryptedPassword(passwordEncoder.encode(rawPassword))
                .displayName(displayName)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();
        memberRepository.save(member);
    }
}
