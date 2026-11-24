package com.ihu.escapehub.team;

import com.ihu.escapehub.user.User;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    private User creator;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> members = new HashSet<>();

    protected Team() {
    }

    public Team(String name, User creator) {
        this.name = name;
        this.creator = creator;
        this.members.add(creator);
    }

    public boolean isCreator(User u) {
        return creator != null && creator.equals(u);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }
}