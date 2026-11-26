package com.ihu.escapehub.team;

import com.ihu.escapehub.user.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team createTeam(String name, User user) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Το όνομα της ομάδας δεν μπορεί να είναι κενό.");
        }
        String cleanName = name.trim();
        if (teamRepository.existsByName(cleanName)) {
            throw new IllegalArgumentException("Υπάρχει ήδη ομάδα με αυτό το όνομα.");
        }
        Team team = new Team(cleanName, user);
        return teamRepository.save(team);
    }

    public void joinTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Η ομάδα δεν βρέθηκε."));

        if (team.getMembers().contains(user)) {
            throw new IllegalArgumentException("Είσαι ήδη μέλος αυτής της ομάδας.");
        }
        team.getMembers().add(user);
        teamRepository.save(team);
    }

    public List<Team> listTeams() {
        return teamRepository.findAll();
    }

    public List<Team> myTeams(User user) {
        return teamRepository.findByMembersContaining(user);
    }
}