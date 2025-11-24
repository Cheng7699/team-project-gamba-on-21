package use_case.readRule;

public class ReadRuleInteractor implements ReadRuleInputBoundary {
    private final ReadRuleOutputBoundary presenter;

    public ReadRuleInteractor(ReadRuleOutputBoundary readRulePresenter) {
        this.presenter = readRulePresenter;
    }

    @Override
    public void execute(ReadRuleInputData readRuleInputData) {
        String rules = "\"<html>\"\n" +
                "    + \"<b>Blackjack Rules and Odds:</b><br>\"\n" +
                "    + \"<ul>\"\n" +
                "    + \"<li>Game uses one player and one dealer with a standard 52-card deck.</li>\"\n" +
                "    + \"<li>Cards keep face value; face cards = 10, Aces = 1 or 11.</li>\"\n" +
                "    + \"<li>Both player and dealer start with two cards.</li>\"\n" +
                "    + \"<li>Player cards are face up; dealer shows one and hides one.</li>\"\n" +
                "    + \"<li>Goal: reach a total as close to 21 as possible without going over.</li>\"\n" +
                "    + \"<li>Player may Hit or Stand.</li>\"\n" +
                "    + \"<li>Dealer must hit until 17, then stand.</li>\"\n" +
                "    + \"<li>Blackjack beats all hands except dealer Blackjack (push).</li>\"\n" +
                "    + \"<li>Player bust over 21 loses; dealer bust gives player win.</li>\"\n" +
                "    + \"<li>Odds: Player wins ~42–44% of hands.</li>\"\n" +
                "    + \"<li>Dealer wins ~48–49% of hands.</li>\"\n" +
                "    + \"<li>Pushes occur ~8–9% of the time.</li>\"\n" +
                "    + \"<li>Chance of being dealt Blackjack: ~4.83%.</li>\"\n" +
                "    + \"</ul>\"\n" +
                "    + \"</html>\"";
        ReadRuleOutputData outputData = new ReadRuleOutputData(rules);
        presenter.present(outputData);
    }
}
