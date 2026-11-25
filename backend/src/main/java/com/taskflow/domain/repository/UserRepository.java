package com.taskflow.domain.repository;

import com.taskflow.domain.model.User;
import java.util.List;
import java.util.Optional;

/** Outbound port: persistence of users. Implemented by the infrastructure layer. */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
}
