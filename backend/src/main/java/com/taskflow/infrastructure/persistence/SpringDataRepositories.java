package com.taskflow.infrastructure.persistence;

import com.taskflow.domain.model.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Spring Data interfaces: an implementation detail of the persistence adapters. */
final class SpringDataRepositories {

    private SpringDataRepositories() {}

    interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
        Optional<UserJpaEntity> findByEmail(String email);
        boolean existsByEmail(String email);
    }

    interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, Long> {
        @Query("select p from ProjectJpaEntity p join p.members m where key(m) = :userId order by p.createdAt desc")
        List<ProjectJpaEntity> findAllByMember(@Param("userId") Long userId);
    }

    interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long> {
        Optional<TaskJpaEntity> findByIdAndProjectId(Long id, Long projectId);

        @Query("select t from TaskJpaEntity t where t.projectId = :projectId "
                + "and (:status is null or t.status = :status) order by t.createdAt desc")
        List<TaskJpaEntity> findByProject(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
    }
}
