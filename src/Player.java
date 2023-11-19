import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Player {
    private final UUID playerId;
    private long balance;
    private int betsPlaced;
    private int betsWon;

    public Player(UUID playerId) {
        this.playerId = playerId;
        this.balance = 0;
        this.betsPlaced = 0;
        this.betsWon = 0;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(int amount){
        this.balance += amount;
    }


    public int bet(int amount, char side, Match match){
        betsPlaced+=1;
        char result = match.getResult();
        int balanceChange = 0;

        // In case of a win
        if (side == result){
            betsWon += 1;
            balanceChange = match.calculateWinnings(amount);
            this.balance += balanceChange;
        }
        // In case of a loss
        else if (result =='A' || result == 'B') {
            balanceChange = -amount;
            this.balance -= amount;
        }
        return balanceChange;
    }

    public void withdraw(int amount){
        this.balance -= amount;
    }

    public BigDecimal getWinrate(){
        if (betsPlaced == 0)
            return BigDecimal.ZERO;
        return new BigDecimal(betsWon).divide(new BigDecimal(betsPlaced), 2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return playerId + " " + balance + " " + getWinrate();
    }
}
