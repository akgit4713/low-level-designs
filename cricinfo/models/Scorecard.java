package cricinfo.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the complete scorecard of a match.
 * Uses Builder pattern for construction.
 */
public class Scorecard {
    private final String matchId;
    private final List<InningsScorecard> inningsCards;
    private final String matchResult;
    private final String manOfTheMatch;

    private Scorecard(Builder builder) {
        this.matchId = builder.matchId;
        this.inningsCards = builder.inningsCards;
        this.matchResult = builder.matchResult;
        this.manOfTheMatch = builder.manOfTheMatch;
    }

    public String getMatchId() {
        return matchId;
    }

    public List<InningsScorecard> getInningsCards() {
        return Collections.unmodifiableList(inningsCards);
    }

    public String getMatchResult() {
        return matchResult;
    }

    public String getManOfTheMatch() {
        return manOfTheMatch;
    }

    public String getFullScorecard() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("SCORECARD\n");
        sb.append("=".repeat(60)).append("\n\n");

        for (InningsScorecard inningsCard : inningsCards) {
            sb.append(inningsCard.toString()).append("\n\n");
        }

        if (matchResult != null) {
            sb.append("Result: ").append(matchResult).append("\n");
        }
        if (manOfTheMatch != null) {
            sb.append("Man of the Match: ").append(manOfTheMatch).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getFullScorecard();
    }

    // Builder Pattern
    public static class Builder {
        private String matchId;
        private List<InningsScorecard> inningsCards = new ArrayList<>();
        private String matchResult;
        private String manOfTheMatch;

        public Builder(String matchId) {
            this.matchId = matchId;
        }

        public Builder addInningsScorecard(InningsScorecard inningsCard) {
            this.inningsCards.add(inningsCard);
            return this;
        }

        public Builder setMatchResult(String matchResult) {
            this.matchResult = matchResult;
            return this;
        }

        public Builder setManOfTheMatch(String manOfTheMatch) {
            this.manOfTheMatch = manOfTheMatch;
            return this;
        }

        public Scorecard build() {
            return new Scorecard(this);
        }
    }

    /**
     * Represents scorecard for a single innings.
     */
    public static class InningsScorecard {
        private final Innings innings;

        public InningsScorecard(Innings innings) {
            this.innings = innings;
        }

        public Innings getInnings() {
            return innings;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("-".repeat(50)).append("\n");
            sb.append(innings.getBattingTeam().getName()).append(" Innings\n");
            sb.append("-".repeat(50)).append("\n");

            // Batting
            sb.append("\nBATTING\n");
            sb.append(String.format("%-25s %-25s %5s %5s %3s %3s %6s\n",
                    "Batsman", "Dismissal", "R", "B", "4s", "6s", "SR"));
            
            for (BatsmanScore bs : innings.getBatsmanScores().values()) {
                sb.append(String.format("%-25s %-25s %5d %5d %3d %3d %6.2f\n",
                        bs.getPlayer().getName(),
                        bs.getDismissalString(),
                        bs.getRuns(),
                        bs.getBallsFaced(),
                        bs.getFours(),
                        bs.getSixes(),
                        bs.getStrikeRate()));
            }
            
            sb.append(String.format("\nExtras: %d\n", innings.getExtras()));
            sb.append(String.format("Total: %s\n", innings.getScoreWithOvers()));

            // Bowling
            sb.append("\nBOWLING\n");
            sb.append(String.format("%-25s %6s %3s %5s %3s %6s\n",
                    "Bowler", "O", "M", "R", "W", "Econ"));
            
            for (BowlerStats bs : innings.getBowlerStats().values()) {
                sb.append(String.format("%-25s %6s %3d %5d %3d %6.2f\n",
                        bs.getPlayer().getName(),
                        bs.getOversString(),
                        bs.getMaidens(),
                        bs.getRunsConceded(),
                        bs.getWickets(),
                        bs.getEconomyRate()));
            }

            // Fall of wickets
            if (!innings.getFallOfWickets().isEmpty()) {
                sb.append("\nFall of Wickets: ");
                sb.append(String.join(", ", innings.getFallOfWickets()));
                sb.append("\n");
            }

            return sb.toString();
        }
    }
}



