package cricinfo.models;

import cricinfo.enums.BattingStyle;
import cricinfo.enums.BowlingStyle;
import cricinfo.enums.PlayerRole;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a cricket player with their personal and career information.
 */
public class Player {
    private final String id;
    private String name;
    private String country;
    private LocalDate dateOfBirth;
    private PlayerRole role;
    private BattingStyle battingStyle;
    private BowlingStyle bowlingStyle;
    private PlayerStats stats;

    public Player(String name, String country) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.country = country;
        this.stats = new PlayerStats();
    }

    public Player(String id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.stats = new PlayerStats();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public PlayerRole getRole() {
        return role;
    }

    public BattingStyle getBattingStyle() {
        return battingStyle;
    }

    public BowlingStyle getBowlingStyle() {
        return bowlingStyle;
    }

    public PlayerStats getStats() {
        return stats;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public void setBattingStyle(BattingStyle battingStyle) {
        this.battingStyle = battingStyle;
    }

    public void setBowlingStyle(BowlingStyle bowlingStyle) {
        this.bowlingStyle = bowlingStyle;
    }

    public void setStats(PlayerStats stats) {
        this.stats = stats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", role=" + role +
                '}';
    }
}



