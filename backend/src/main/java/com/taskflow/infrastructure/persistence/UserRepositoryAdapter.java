package com.taskflow.infrastructure.persistence;

import com.taskflow.domain.model.User;
import com.taskflow.domain.repository.UserRepository;
import com.taskflow.infrastructure.persistence.SpringDataRepositories.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public User save(User user) {
        return jpa.save(UserJpaEntity.from(user)).toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return jpa.findAll().stream().map(UserJpaEntity::toDomain).toList();
    }
}
