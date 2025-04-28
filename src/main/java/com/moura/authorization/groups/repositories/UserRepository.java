package com.moura.authorization.groups.repositories;

import com.moura.authorization.auth.entities.Credentials;
import com.moura.authorization.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
      SELECT u FROM User u
      LEFT JOIN FETCH u.groups g
      LEFT JOIN FETCH g.permissions
      WHERE u.email = :email
    """)
    Optional<User> findByEmail(@Param("email") String username);

    @Override
    @Query("""
      SELECT u FROM User u
      LEFT JOIN FETCH u.groups g
      LEFT JOIN FETCH g.permissions
      WHERE u.id = :id
    """)
    Optional<User> findById(@Param("id") UUID id);
}
