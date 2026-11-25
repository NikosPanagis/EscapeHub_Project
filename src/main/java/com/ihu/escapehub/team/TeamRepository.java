package com.ihu.escapehub.team;

import com.ihu.escapehub.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    List<Team> findByMembersContaining(User user);
}