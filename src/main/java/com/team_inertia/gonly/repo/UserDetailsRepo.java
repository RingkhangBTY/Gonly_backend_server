package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepo extends JpaRepository<User, Long> {

    // ✅ FIXED: Only email-based lookups (email is UNIQUE in your schema)
    User findByEmail(String email);

    Boolean existsByEmail(String email);
}