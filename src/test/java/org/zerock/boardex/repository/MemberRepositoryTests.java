package org.zerock.boardex.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.Commit;
import org.zerock.boardex.domain.Member;
import org.zerock.boardex.domain.MemberRole;
import org.zerock.boardex.repository.MemberRepository;

import java.util.Optional;
import java.util.stream.IntStream;

//회원가입 데이터 추가하기
@SpringBootTest
@Log4j2
class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMember(){
        IntStream.rangeClosed(1, 100).forEach(i->{
            Member member = Member.builder()
                    .mid("member"+i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email" + i + "@gmail.com")
                    .build();
            //모든 멤버에 USER 사용자 권한 추가
            member.addRole(MemberRole.USER);

            //i 값이 90 이상인 멤버에 ADMIN 관리자 권한 추가
            if(i >= 90){
                member.addRole(MemberRole.ADMIN);
            }
            //DB에 데이터 저장
            memberRepository.save(member);
        });
    }

    //회원 조회 테스트
    @Test
    public void testRead(){
        //데이터베이스에서 멤버아이디가 member100인 데이터의 권한을 찾아서 result에 저장.
        Optional<Member> result = memberRepository.getWithRoles("member100");
        //result 값에 오류가 있으면 예외처리
        Member member = result.orElseThrow();
        //member 객체의 정보 표시
        log.info(member);
        //member100객체의 설정된 권한 표시
        log.info(member.getRoleSet());

        //권한이 USER, ADMIN 2개이므로 2번 반복
        member.getRoleSet().forEach(memberRole -> {
            //권한 이름을 콘솔에 표시
            log.info(memberRole.name());
        });
    }
    //비밀번호 수정하기 테스트
    @Commit
    @Test
    public void testUpdate(){
        String mid="email1@gmail.com";
        String mpw= passwordEncoder.encode("2222");
        memberRepository.updatePassword(mpw, mid);
    }

}