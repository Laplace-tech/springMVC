package hello.springmvc2.web.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import hello.springmvc2.domain.item.entry.Item;
import hello.springmvc2.domain.item.repository.ItemRepository;
import hello.springmvc2.domain.member.entry.Member;
import hello.springmvc2.domain.member.repository.MemberRepository;
import hello.springmvc2.upload.domain.UploadFile;
import hello.springmvc2.upload.file.FileStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

	private final FileStore fileStore;
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
        try {
            // 첨부파일
            UploadFile attachFile = fileStore.storeFile(
                new ClassPathResource("static/images/lunisha.jpg").getFile()
            );

            // 이미지 파일
            List<UploadFile> imageFiles = List.of(
                fileStore.storeFile(new ClassPathResource("static/images/lunisha1.jpg").getFile()),
                fileStore.storeFile(new ClassPathResource("static/images/lunisha2.jpg").getFile())
            );

            // 테스트 아이템 생성
            Item item = Item.builder()
                    .itemName("테스트 아이템1")
                    .price(10_000)
                    .quantity(10)
                    .attachFile(attachFile)
                    .imageFiles(imageFiles)
                    .build();

            itemRepository.save(item);

            log.info("테스트 아이템 초기화 완료: {}", item.getItemName());

        } catch (IOException e) {
            log.warn("테스트 아이템 초기화 실패", e);
        }
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
