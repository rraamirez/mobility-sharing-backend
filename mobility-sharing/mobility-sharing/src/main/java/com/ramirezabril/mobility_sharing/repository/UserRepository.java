package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.id FROM users u", nativeQuery = true)
    List<Integer> getUserIds();
}

