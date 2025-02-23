package com.ramirezabril.mobility_sharing.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
                SELECT t FROM Token t 
                WHERE t.user.id = :id 
                AND (t.isExpired = false OR t.isRevoked = false)
            """)
    List<Token> findAllValidTokenByUser(@Param("id") Integer id);


    Optional<Token> findByToken(String token);
}