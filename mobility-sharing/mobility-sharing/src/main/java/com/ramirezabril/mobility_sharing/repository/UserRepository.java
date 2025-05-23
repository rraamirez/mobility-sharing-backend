package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.id FROM users u", nativeQuery = true)
    List<Integer> getUserIds();

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET rating = :rating WHERE id = :userId", nativeQuery = true)
    void updateUserRating(@Param("userId") Integer userId, @Param("rating") int rating);
}

