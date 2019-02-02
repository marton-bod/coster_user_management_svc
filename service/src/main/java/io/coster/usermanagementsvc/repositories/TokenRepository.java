package io.coster.usermanagementsvc.repositories;

import io.coster.usermanagementsvc.domain.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<AuthToken, String> {
    Optional<AuthToken> findByUserIdAndAuthToken(String userId, String token);
}
