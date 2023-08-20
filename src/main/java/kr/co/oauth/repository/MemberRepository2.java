package kr.co.oauth.repository;


import kr.co.oauth.domain.Member2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository2 extends JpaRepository<Member2, Long> {
    Optional<Member2> findByEmailAndProvider(String email, String provider);
}
