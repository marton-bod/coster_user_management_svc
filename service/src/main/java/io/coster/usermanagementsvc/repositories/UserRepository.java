package io.coster.usermanagementsvc.repositories;

import io.coster.usermanagementsvc.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
