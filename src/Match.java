import java.util.UUID;

public class Match {

    private final UUID matchId;
    private char result;
    private double rateA;
    private double rateB;

    public Match(UUID matchId, char result, double rateA, double rateB) {
        this.matchId = matchId;
        this.result = result;
        this.rateA = rateA;
        this.rateB = rateB;
    }

    public UUID getMatchId() {
        return matchId;
    }

    public char getResult() {
        return result;
    }

    public double getRateA() {
        return rateA;
    }

    public double getRateB() {
        return rateB;
    }

    public int calculateWinnings(int betSize){
        int winnings;
        double sideRate;
        if (result=='A')
            sideRate=rateA;
        else
            sideRate=rateB;

        winnings = (int) (betSize*sideRate);

        return winnings;
    }

}
