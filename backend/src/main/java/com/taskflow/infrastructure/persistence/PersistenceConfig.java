package com.taskflow.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** The Spring Data interfaces are nested in {@link SpringDataRepositories}, hence considerNestedRepositories. */
@Configuration
@EnableJpaRepositories(considerNestedRepositories = true)
class PersistenceConfig {}
