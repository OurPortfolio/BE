package com.sparta.ourportfolio.user.repository;

import com.sparta.ourportfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByNaverId(Long naverId);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    boolean existsByNickname(String nickname);
}