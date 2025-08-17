package hello.springmvc2.domain.member.service;


import java.util.List;

import org.springframework.stereotype.Service;

import hello.springmvc2.domain.member.controller.form.MemberSaveForm;
import hello.springmvc2.domain.member.controller.form.MemberUpdateForm;
import hello.springmvc2.domain.member.controller.mapper.MemberMapper;
import hello.springmvc2.domain.member.dto.MemberDto;
import hello.springmvc2.domain.member.entry.Member;
import hello.springmvc2.domain.member.exception.MemberNotFoundException;
import hello.springmvc2.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private static final String NOT_FOUND_BY_ID = "회원이 존재하지 않습니다. id=%s";
	private static final String NOT_FOUND_BY_LOGIN_ID = "회원이 존재하지 않습니다. loginId=%s";
    
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberDto getMemberDto(Long id) {
        return memberMapper.toViewDto(getMemberOrThrow(id));
    }

    public MemberDto getMemberDto(String loginId) {
    	return memberMapper.toViewDto(getMemberOrThrow(loginId));
    }
    
    public MemberUpdateForm getMemberUpdateForm(Long id) {
        return memberMapper.toForm(getMemberOrThrow(id));
    }

    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(memberMapper::toViewDto)
                .toList();
    }

    public MemberDto registerMember(MemberSaveForm form) {
        Member member = memberMapper.toEntity(form);
        member = memberRepository.save(member);
        return memberMapper.toViewDto(member);
    }

    public MemberDto updateMember(Long id, MemberUpdateForm form) {
        validateIdMatch(id, form.getId());

        Member existingMember = getMemberOrThrow(id);
        Member updatedMember = memberMapper.toEntity(existingMember, form);

        if (!memberRepository.update(updatedMember)) {
            throw new MemberNotFoundException(String.format(NOT_FOUND_BY_ID, id));
        }
        return memberMapper.toViewDto(updatedMember);
    }

    public void deleteMember(Long id) {
        if (!memberRepository.delete(id)) {
            throw new MemberNotFoundException(String.format(NOT_FOUND_BY_ID, id));
        }
    }

    private Member getMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
    }
    
    private Member getMemberOrThrow(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(String.format(NOT_FOUND_BY_LOGIN_ID, loginId)));
    }

    public boolean existsByLoginId(String loginId) {
    	return memberRepository.findByLoginId(loginId).isPresent();
    }
    
    private void validateIdMatch(Long pathId, Long formId) {
        if (!pathId.equals(formId)) {
            throw new IllegalArgumentException(
                String.format("Path variable id(%d)와 form의 id(%d)가 일치하지 않습니다.", pathId, formId)
            );
        }
    }
}
