package app.springrestful.member.domain.repository;

import app.springrestful.member.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by taesu at : 2019-02-21
 *
 * 여기에 MemberRepository 인터페이스에 대한 설명을 기술해주세요
 *
 * @author taesu
 * @version 1.0
 * @since 1.0
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
