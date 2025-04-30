package com.moura.authorization.users.repositories;


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
    Optional<User> findByEmail(@Param("email") String email);

    @Override
    @Query("""
      SELECT u FROM User u
      LEFT JOIN FETCH u.groups g
      LEFT JOIN FETCH g.permissions
      WHERE u.id = :id AND u.userStatus = 'ACTIVE'
    """)
    Optional<User> findById(@Param("id") UUID id);

    @Query("""
      SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
      FROM User u
      WHERE u.email = :email AND u.userStatus = 'ACTIVE'
    """)
    boolean existsByEmail(String email);
}
