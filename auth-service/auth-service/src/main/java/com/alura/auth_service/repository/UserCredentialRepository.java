package com.alura.auth_service.repository;

import com.alura.auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    Optional<UserCredential> findByEmail(String email);

    Optional<UserCredential> findByGithubId(Long githubId);
}
