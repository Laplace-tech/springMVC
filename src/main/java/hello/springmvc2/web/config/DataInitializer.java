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
	private final BCryptPasswordEncoder passwordEncdoer;

	@PostConstruct
	public void run() {
		// 초기 테스트 데이터 삽입
		itemRepository.save(new Item(null, "테스트 아이템1", 10000, 10));
		itemRepository.save(new Item(null, "테스트 아이템2", 20000, 20));
		itemRepository.save(new Item(null, "테스트 아이템3", 30000, 30));

		Member member1 = Member.builder().loginId("Anna").encryptedPassword(passwordEncdoer.encode("28482848a")).displayName("Anna")
				.createdAt(LocalDateTime.now()).lastModifiedAt(LocalDateTime.now()).build();
		Member member2 = Member.builder().loginId("Erma").encryptedPassword(passwordEncdoer.encode("28482848a")).displayName("Erma")
				.createdAt(LocalDateTime.now()).lastModifiedAt(LocalDateTime.now()).build();
		memberRepository.save(member1);
		memberRepository.save(member2);
	}

}
