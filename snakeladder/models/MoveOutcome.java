package snakeladder.models;

import snakeladder.enums.MoveResult;

/**
 * Represents the outcome of a player's move.
 * Immutable value object containing all move details.
 */
public class MoveOutcome {
    
    private final Player player;
    private final int diceValue;
    private final int fromPosition;
    private final int toPosition;
    private final int finalPosition;  // After snake/ladder adjustment
    private final MoveResult result;
    private final BoardElement encounteredElement;  // Snake or Ladder if any

    private MoveOutcome(Builder builder) {
        this.player = builder.player;
        this.diceValue = builder.diceValue;
        this.fromPosition = builder.fromPosition;
        this.toPosition = builder.toPosition;
        this.finalPosition = builder.finalPosition;
        this.result = builder.result;
        this.encounteredElement = builder.encounteredElement;
    }

    public Player getPlayer() {
        return player;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public int getFinalPosition() {
        return finalPosition;
    }

    public MoveResult getResult() {
        return result;
    }

    public BoardElement getEncounteredElement() {
        return encounteredElement;
    }

    public boolean hasEncounteredElement() {
        return encounteredElement != null;
    }

    public boolean isWinningMove() {
        return result == MoveResult.WON;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(player.getPiece().getEmoji()).append(" ")
          .append(player.getName())
          .append(" rolled ").append(diceValue)
          .append(": ").append(fromPosition).append(" → ").append(toPosition);
        
        if (hasEncounteredElement()) {
            sb.append(" → ").append(finalPosition)
              .append(" (").append(encounteredElement.getDescription()).append(")");
        }
        
        if (isWinningMove()) {
            sb.append(" 🏆 WINNER!");
        }
        
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Player player;
        private int diceValue;
        private int fromPosition;
        private int toPosition;
        private int finalPosition;
        private MoveResult result;
        private BoardElement encounteredElement;

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public Builder diceValue(int diceValue) {
            this.diceValue = diceValue;
            return this;
        }

        public Builder fromPosition(int fromPosition) {
            this.fromPosition = fromPosition;
            return this;
        }

        public Builder toPosition(int toPosition) {
            this.toPosition = toPosition;
            return this;
        }

        public Builder finalPosition(int finalPosition) {
            this.finalPosition = finalPosition;
            return this;
        }

        public Builder result(MoveResult result) {
            this.result = result;
            return this;
        }

        public Builder encounteredElement(BoardElement element) {
            this.encounteredElement = element;
            return this;
        }

        public MoveOutcome build() {
            return new MoveOutcome(this);
        }
    }
}



