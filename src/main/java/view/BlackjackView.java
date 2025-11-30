package view;

import entity.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.StringJoiner;

/**
 * The Blackjack game view. It provides controls for the key player actions and
 * displays the user's balance and current bet
 */
public class BlackjackView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "blackjack";

    private final String viewName = VIEW_NAME;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    private final JLabel balanceValueLabel = new JLabel("$0");
    private final JLabel betValueLabel = new JLabel("$0");
    private final JSpinner betSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10_000, 1));
    private final JLabel statusLabel = new JLabel("Place your bet to start playing.");
    private int actualBalance = 0; // stores the actual balance before bet deduction

    private final JLabel dealerHandLabel = new JLabel("Dealer: -");
    private final JLabel playerHandLabel = new JLabel("Player: -");

    private final JButton hitButton = new JButton("Hit");
    private final JButton standButton = new JButton("Stand");
    private final JButton splitButton = new JButton("Split");
    private final JButton rulesButton = new JButton("Rules");
    private final JButton quitButton = new JButton("Quit");
    private final JButton placeBetButton = new JButton("Place Bet");
    private final JButton newRoundButton = new JButton("New Round");

    private BlackjackGame game;
    private Hand playerHand = new Hand("");
    private Hand splitHand;
    private Hand dealerHand = new Hand("");
    private boolean hideDealerHoleCard;
    private boolean betLocked;
    private boolean roundActive;
    private boolean playingSplitHand;

    private ActionListener hitActionListener;
    private ActionListener standActionListener;
    private ActionListener splitActionListener;
    private ActionListener gameStartActionListener;
    private interface_adapter.placeBet.PlaceBetController placeBetController;

    public BlackjackView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel.addPropertyChangeListener(this);
        this.game = new BlackjackGame(null,
                new BlackjackDealer(),
                new BlackjackPlayer(loggedInViewModel.getState().getUsername()));

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        final JLabel titleLabel = new JLabel("Blackjack");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 2, 8, 8));
        infoPanel.add(new JLabel("Current Balance:"));
        infoPanel.add(balanceValueLabel);
        infoPanel.add(new JLabel("Current Bet:"));
        infoPanel.add(betValueLabel);

        final JPanel betPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        betPanel.add(new JLabel("Adjust Bet:"));
        betPanel.add(betSpinner);
        betPanel.add(placeBetButton);
        betPanel.add(newRoundButton);

        final JPanel handPanel = new JPanel();
        handPanel.setLayout(new GridLayout(2, 1, 8, 8));
        handPanel.setBorder(BorderFactory.createTitledBorder("Hands"));
        dealerHandLabel.setPreferredSize(new Dimension(400, dealerHandLabel.getPreferredSize().height));
        playerHandLabel.setPreferredSize(new Dimension(400, playerHandLabel.getPreferredSize().height));
        handPanel.add(dealerHandLabel);
        handPanel.add(playerHandLabel);

        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(handPanel);
        centerPanel.add(Box.createVerticalGlue());
        add(centerPanel, BorderLayout.CENTER);
        add(betPanel, BorderLayout.WEST);

        final JPanel controlsPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        controlsPanel.add(hitButton);
        controlsPanel.add(standButton);
        controlsPanel.add(splitButton);
        controlsPanel.add(rulesButton);
        controlsPanel.add(quitButton);

        final JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.add(controlsPanel);

        final JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        footerPanel.add(statusPanel);

        add(footerPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        splitButton.addActionListener(this);
        rulesButton.addActionListener(this);
        quitButton.addActionListener(this);
        placeBetButton.addActionListener(this);
        newRoundButton.addActionListener(this);

        betSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int betAmount = (Integer) betSpinner.getValue();
                betValueLabel.setText("$" + betAmount);
                updateBalanceDisplay();
            }
        });

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        splitButton.setEnabled(false);
        newRoundButton.setEnabled(false);

        final LoggedInState initialState = loggedInViewModel.getState();
        if (initialState != null) {
            updateBalance(initialState);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final Object source = evt.getSource();
        if (source.equals(quitButton)) {
            navigateTo("logged in");
        }
        else if (source.equals(rulesButton)) {
            navigateTo("rules");
        }
        else if (source.equals(placeBetButton)) {
            confirmBetAndStartRound();
            if (gameStartActionListener != null) {
                gameStartActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "newRound"));
            }
        }
        else if (source.equals(newRoundButton)) {
            resetRound();
        }
        else if (source.equals(hitButton)) {
            playerHits();
        }
        else if (source.equals(standButton)) {
            playerStands();
        }
        else if (source.equals(splitButton)) {
            playerSplits();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) || "balance".equals(evt.getPropertyName())) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            updateBalance(state);
        }
    }

    private void updateBalance(LoggedInState state) {
        if (state != null) {
            actualBalance = state.getBalance();
            updateBalanceDisplay();
            updateBetSpinnerMax();
        }
    }

    // updates the balance display to show balance after bet deduction
    private void updateBalanceDisplay() {
        if (!betLocked) {
            int betAmount = (Integer) betSpinner.getValue();
            int balanceAfterBet = actualBalance - betAmount;
            balanceValueLabel.setText("$" + balanceAfterBet);
            
            // change color if insufficient funds
            if (balanceAfterBet < 0 || betAmount > actualBalance) {
                balanceValueLabel.setForeground(Color.RED);
            } else {
                balanceValueLabel.setForeground(Color.BLACK);
            }
        } else {
            // when bet is locked, show actual balance (which includes winnings/losses after game ends)
            balanceValueLabel.setText("$" + actualBalance);
            balanceValueLabel.setForeground(Color.BLACK);
        }
    }

    // updates the spinner's maximum value to current balance
    private void updateBetSpinnerMax() {
        if (!betLocked) {
            SpinnerNumberModel model = (SpinnerNumberModel) betSpinner.getModel();
            int currentMax = (Integer) model.getMaximum();
            if (actualBalance != currentMax) {
                int currentValue = (Integer) betSpinner.getValue();
                // ensure current value doesn't exceed new max
                if (currentValue > actualBalance) {
                    betSpinner.setValue(actualBalance);
                }
                // set max to actualBalance (or 0 if balance is negative)
                model.setMaximum(Math.max(0, actualBalance));
            }
        }
    }

    private void navigateTo(String destinationView) {
        viewManagerModel.setState(destinationView);
        viewManagerModel.firePropertyChange();
    }

    private void confirmBetAndStartRound() {
        if (betLocked) {
            statusLabel.setText("Bet is already locked for this round.");
            return;
        }

        final int selectedBet = (Integer) betSpinner.getValue();
        if (selectedBet <= 0) {
            statusLabel.setText("Please choose a bet greater than $0 to start.");
            return;
        }

        // check if player has sufficient funds - prevent placing bet if bet > balance
        if (selectedBet > actualBalance) {
            statusLabel.setText("Insufficient funds! Your balance is $" + actualBalance + ". Please adjust your bet.");
            balanceValueLabel.setForeground(Color.RED);
            // reset bet spinner to balance if it exceeds
            if (actualBalance > 0) {
                betSpinner.setValue(actualBalance);
            } else {
                betSpinner.setValue(0);
            }
            return;
        }

        betLocked = true;
        betSpinner.setEnabled(false);
        placeBetButton.setEnabled(false);
        betValueLabel.setText("$" + selectedBet);
        
        // show actual balance after bet is placed
        updateBalanceDisplay();
        
        // set bet amount in game
        if (game != null) {
            game.setBetAmount(selectedBet);
        }

        // refactoring: call controller execute from view layer (clean architecture)
        if (placeBetController != null) {
            placeBetController.execute(selectedBet);
        }

        startRound();
    }

    private void startRound() {

        roundActive = true;
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
        splitButton.setEnabled(true);
        newRoundButton.setEnabled(false);
        playingSplitHand = false;
        splitHand = null;

        updateHandLabels(hideDealerHoleCard);
        statusLabel.setText("Bet locked. Your round has started!");
    }

    private void resetRound() {
        betLocked = false;
        roundActive = false;
        betSpinner.setEnabled(true);
        placeBetButton.setEnabled(true);
        newRoundButton.setEnabled(false);
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        splitButton.setEnabled(false);
        
        // reset bet spinner to 0 after game finishes
        betSpinner.setValue(0);
        betValueLabel.setText("$0");
        
        // restore balance preview and update spinner max
        // balance should already be updated from payout, but refresh display
        updateBetSpinnerMax();
        updateBalanceDisplay();
        
        playerHand = null;
        splitHand = null;
        dealerHand = null;
        hideDealerHoleCard = false;
        playingSplitHand = false;

        playerHandLabel.setText("Player: -");
        dealerHandLabel.setText("Dealer: -");
        statusLabel.setText("Place your bet to start playing.");

    }

    private void playerHits() {
        if (!roundActive) {
            statusLabel.setText("Start a round by placing a bet first.");
            return;
        }

        statusLabel.setText("Hit chosen. Waiting for result...");
        if (hitActionListener != null) {
            boolean isInSplitHand = game != null && game.isSplitted() && playingSplitHand;
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, isInSplitHand ? "hitSplit" : "hit");
            hitActionListener.actionPerformed(event);
        }
    }

    private void playerStands() {
        if (!roundActive) {
            statusLabel.setText("Start a round by placing a bet first.");
            return;
        }

        if (game != null && game.isSplitted() && !playingSplitHand) {
            advanceToSplitHand();
            return;
        }

        statusLabel.setText("Stand chosen. Waiting for dealer...");
        if (standActionListener != null) {
            standActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "stand"));
        }
    }

    private void playerSplits() {
        if (!roundActive) {
            statusLabel.setText("Start a round by placing a bet first.");
            return;
        }

        if (splitActionListener != null) {
            splitActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "split"));
        }
    }

    private void endRound(String message) {
        roundActive = false;
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        splitButton.setEnabled(false);
        newRoundButton.setEnabled(true);
        updateHandLabels(false);
        
        // reset bet spinner to 0 after game finishes
        betSpinner.setValue(0);
        betValueLabel.setText("$0");
        
        // unlock bet controls so user can place new bet
        betLocked = false;
        betSpinner.setEnabled(true);
        placeBetButton.setEnabled(true);
        
        // balance should already be updated from payout, but refresh display
        updateBalanceDisplay();
        updateBetSpinnerMax();
        
        statusLabel.setText(message + " Click New Round to place another bet.");
    }

    private void updateHandLabels(boolean hideDealerHoleCard) {
        dealerHandLabel.setText(formatHandLabel("Dealer", dealerHand, hideDealerHoleCard));
        if (splitHand != null) {
            String playerHandsHtml = "<html>" +
                    formatHandLabel("Player Hand 1", playerHand, false) +
                    "<br/>" +
                    formatHandLabel("Player Hand 2", splitHand, false) +
                    "</html>";
            playerHandLabel.setText(playerHandsHtml);
        } else {
            playerHandLabel.setText(formatHandLabel("Player", playerHand, false));
        }
    }

    private String formatHandLabel(String owner, Hand hand, boolean hideHoleCard) {
        if (hand == null || hand.getCards().isEmpty()) {
            return owner + ": -";
        }

        final StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < hand.getCards().size(); i++) {
            final Card card = hand.getCards().get(i);
            if (hideHoleCard && owner.equals("Dealer") && i == 0) {
                joiner.add("[Hidden]");
            }
            else {
                joiner.add(describeCard(card));
            }
        }

        final String totalText;
        if (hideHoleCard && owner.equals("Dealer")) {
            totalText = "?";
        }
        else {
            totalText = String.valueOf(hand.getHandTotalNumber());
        }

        return owner + ": " + joiner + " (Total: " + totalText + ")";
    }

    private String describeCard(Card card) {
        return card.getValue() + " of " + card.getSuit();
    }

    public void showDealerCard() {
        updateHandLabels(false);
    }

    public String getViewName() {
        return viewName;
    }

    /**
     * Display the current player and dealer hands from the entity layer.
     * @param playerHand the player's hand to show
     * @param dealerHand the dealer's hand to show
     * @param hideDealerHoleCard whether to hide the dealer's first card
     */
    public void setHands(Hand playerHand, Hand dealerHand, boolean hideDealerHoleCard) {
        this.playerHand = playerHand;
        this.splitHand = null;
        this.dealerHand = dealerHand;
        this.hideDealerHoleCard = hideDealerHoleCard;
        updateHandLabels(hideDealerHoleCard);
    }

    public void setHands(Hand playerHand, Hand splitHand, Hand dealerHand, boolean hideDealerHoleCard) {
        this.playerHand = playerHand;
        this.splitHand = splitHand;
        this.dealerHand = dealerHand;
        this.hideDealerHoleCard = hideDealerHoleCard;
        updateHandLabels(hideDealerHoleCard);
    }

    public Hand getDealerHand() { return dealerHand; }
    public Hand getPlayerHand() { return playerHand; }
    public Hand getSplitHand() { return splitHand; }
    public boolean isHideDealerHoleCard() { return hideDealerHoleCard; }
    public boolean isPlayingSplitHand() { return playingSplitHand; }

    /**
     * Update the UI to reflect the end-of-round state after entity logic completes.
     * @param message status to display to the user
     */
    public void showRoundResult(String message) {
        endRound(message);
    }

    public void setHitActionListener(ActionListener hitActionListener) {
        this.hitActionListener = hitActionListener;
    }

    public void setStandActionListener(ActionListener standActionListener) {
        this.standActionListener = standActionListener;
    }

    public void setSplitActionListener(ActionListener splitActionListener) {
        this.splitActionListener = splitActionListener;
    }

    public void setGameStartActionListener(ActionListener gameStartActionListener) {
        this.gameStartActionListener = gameStartActionListener;
    }

    // refactoring: setter for place bet controller (clean architecture - controller called from view)
    public void setPlaceBetController(interface_adapter.placeBet.PlaceBetController placeBetController) {
        this.placeBetController = placeBetController;
    }


    public void setGame(BlackjackGame game) { this.game = game; }

    public BlackjackGame getGame() { return this.game; }

    public void showStatusMessage(String message) {
        statusLabel.setText(message);
    }

    public void advanceToSplitHand() {
        if (splitHand != null) {
            playingSplitHand = true;
            showStatusMessage("First hand finished. Playing split hand now.");
            updateHandLabels(hideDealerHoleCard);
        }
    }
    
    public JSpinner getBetSpinner() { return this.betSpinner; }
}
