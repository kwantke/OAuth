package kr.co.oauth.user.repository;

import kr.co.oauth.user.entity.ERole;
import kr.co.oauth.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

  Optional<Role> findByName(ERole name);
}
