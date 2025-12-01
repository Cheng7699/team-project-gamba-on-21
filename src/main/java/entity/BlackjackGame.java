package entity;

public class BlackjackGame {
    private String state;
    private String deckID;
    private final BlackjackDealer dealer;
    private final BlackjackPlayer player;
    private boolean isSplitted;
    private float betAmount;
    private float odds;
    private String result;
    private String secondResult = "";

    public BlackjackGame(String deckID, BlackjackDealer dealer, BlackjackPlayer player) {
        this.deckID = deckID;
        this.dealer = dealer;
        this.player = player;
        this.betAmount = 0;
        this.odds = 1;
        this.result = "InGame";
        this.isSplitted = false;
    }

    public String getState() { return state; }
    public String getDeckID() { return deckID; }
    public BlackjackDealer getDealer() { return dealer; }
    public BlackjackPlayer getPlayer() { return player; }
    public float getBetAmount() { return betAmount; }
    public String getSecondResult() { return secondResult; }
    public float getOdds() { return odds; }
    public String getResult() { return result; }
    public boolean isSplitted() { return isSplitted; }
    public void resetSplit() { this.isSplitted = false; }
    public void setSplitted(boolean splitted) { this.isSplitted = splitted; }


    public void setDeckID(String deckID) { this.deckID = deckID; }
    public void setBetAmount(float betAmount) { this.betAmount = betAmount; }
    public void setOdds(float odds) { this.odds = odds; }
    public void setResult(String result) { this.result = result; }

    public void toDealerTurn() { state = "DealerTurn"; }
    public void toPlayerTurn() { state = "PlayerTurn"; }
    public void gameStart() { state = "InGame"; }
    public void gameOver() { state = "GameOver"; }
    public void gameStart() { state = "InGame"; }

    public void playerWin() {
        this.gameOver();
        result = "PlayerWin";
    }

    public void push() {
        this.gameOver();
        result = "Push";
    }

    public void playerLose() {
        this.gameOver();
        result = "PlayerLose";
    }


    public void splitPlayerWin() {
        secondResult = "PlayerWin";
    }

    public void splitPlayerPush() {
        secondResult = "Push";
    }

    public void splitPlayerLose() {
        secondResult = "PlayerLose";
    }
}
