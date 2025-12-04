package cricinfo.models;

import java.util.*;

/**
 * Represents a cricket team with its players and information.
 */
public class Team {
    private final String id;
    private String name;
    private String shortName;
    private String country;
    private String logoUrl;
    private Player captain;
    private Player coach;
    private List<Player> squad;
    private TeamStats stats;

    public Team(String name, String country) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.country = country;
        this.squad = new ArrayList<>();
        this.stats = new TeamStats();
    }

    public Team(String id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.squad = new ArrayList<>();
        this.stats = new TeamStats();
    }

    public void addPlayer(Player player) {
        if (!squad.contains(player)) {
            squad.add(player);
        }
    }

    public void removePlayer(Player player) {
        squad.remove(player);
    }

    public Optional<Player> getPlayerById(String playerId) {
        return squad.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst();
    }

    public Optional<Player> getPlayerByName(String playerName) {
        return squad.stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getCountry() {
        return country;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Player getCaptain() {
        return captain;
    }

    public Player getCoach() {
        return coach;
    }

    public List<Player> getSquad() {
        return Collections.unmodifiableList(squad);
    }

    public TeamStats getStats() {
        return stats;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setCaptain(Player captain) {
        this.captain = captain;
    }

    public void setCoach(Player coach) {
        this.coach = coach;
    }

    public void setStats(TeamStats stats) {
        this.stats = stats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", squadSize=" + squad.size() +
                '}';
    }
}



