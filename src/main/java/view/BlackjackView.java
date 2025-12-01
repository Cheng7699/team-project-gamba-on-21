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
    private final JButton doubleDownButton = new JButton("Double Down");
    private final JButton rulesButton = new JButton("Rules");
    private final JButton quitButton = new JButton("Quit");
    private final JButton placeBetButton = new JButton("Place Bet");

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
    private ActionListener doubleDownActionListener;
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

    // Left side panel with bet controls and centered action buttons
        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

    // Bet controls at the top
        final JPanel betPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        betPanel.add(new JLabel("Adjust Bet:"));
        betPanel.add(betSpinner);
        betPanel.add(placeBetButton);
        leftPanel.add(betPanel, BorderLayout.NORTH);

    // split and double down buttons centered
        final JPanel actionButtonsPanel = new JPanel();
        actionButtonsPanel.setLayout(new BoxLayout(actionButtonsPanel, BoxLayout.Y_AXIS));

        splitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        doubleDownButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionButtonsPanel.add(Box.createVerticalGlue());
        actionButtonsPanel.add(splitButton);
        actionButtonsPanel.add(Box.createVerticalStrut(8));
        actionButtonsPanel.add(doubleDownButton);
        actionButtonsPanel.add(Box.createVerticalGlue());

        leftPanel.add(actionButtonsPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Hand panel with dealer and player hands
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

        final JPanel controlsPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        controlsPanel.add(hitButton);
        controlsPanel.add(standButton);
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
        doubleDownButton.addActionListener(this);
        rulesButton.addActionListener(this);
        quitButton.addActionListener(this);
        placeBetButton.addActionListener(this);

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
        doubleDownButton.setEnabled(false);

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
            resetRound();
            confirmBetAndStartRound();
            if (gameStartActionListener != null) {
                gameStartActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "newRound"));
            }
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
        else if (source.equals(doubleDownButton)) {
            playerDoubleDowns();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) || "balance".equals(evt.getPropertyName())) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            updateBalance(state);
            // Update button states when balance changes
            if (roundActive) {
                updateSplitButtonState();
                updateDoubleDownButtonState();
            }
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
            // Before bet is locked, show preview: balance - spinner value
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
            // When bet is locked, show actual balance which already reflects all deductions
            // actualBalance is updated by PlaceBetInteractor (deducts initial bet) and
            // PlayerDoubleDownInteractor (deducts additional bet on double down)
            // So it already shows: startBalance - initialBet - doubleDownBet (if applicable)
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
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
        splitButton.setEnabled(true);
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
        updateSplitButtonState(); // Enable/disable based on conditions
        updateDoubleDownButtonState(); // Enable/disable based on conditions
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
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        splitButton.setEnabled(false);
        doubleDownButton.setEnabled(false);
        
        // reset bet spinner to 0 after game finishes

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

    private void playerDoubleDowns() {
        if (!roundActive) {
            statusLabel.setText("Start a round by placing a bet first.");
            return;
        }

        statusLabel.setText("Double down chosen. Processing...");
        if (doubleDownActionListener != null) {
            boolean isInSplitHand = game != null && game.isSplitted() && playingSplitHand;
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, isInSplitHand ? "doubleDownSplit" : "doubleDown");
            doubleDownActionListener.actionPerformed(event);
        }
    }

    private void endRound(String message) {
        roundActive = false;
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        splitButton.setEnabled(false);
        doubleDownButton.setEnabled(false);
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
        updateSplitButtonState(); // Update button state when hands change
        updateDoubleDownButtonState(); // Update button state when hands change
    }

    public void setHands(Hand playerHand, Hand splitHand, Hand dealerHand, boolean hideDealerHoleCard) {
        this.playerHand = playerHand;
        this.splitHand = splitHand;
        this.dealerHand = dealerHand;
        this.hideDealerHoleCard = hideDealerHoleCard;
        updateHandLabels(hideDealerHoleCard);
        updateSplitButtonState(); // Update button state when hands change
        updateDoubleDownButtonState(); // Update button state when hands change
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

    public void setDoubleDownActionListener(ActionListener doubleDownActionListener) {
        this.doubleDownActionListener = doubleDownActionListener;
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
            // Update button states for split hand
            updateSplitButtonState(); // Split is disabled after splitting
            updateDoubleDownButtonState();
        }
    }

    /**
     * Updates the bet display to show the current bet amount.
     * This is called when the bet is placed or doubled down.
     * @param betAmount the new bet amount to display (includes double down if applicable)
     */
    public void updateBetDisplay(int betAmount) {
        betValueLabel.setText("$" + betAmount);
        // Update balance display to reflect the new bet amount
        updateBalanceDisplay();
    }

    /**
     * Disables all player action buttons (Hit, Stand, Split, Double Down).
     * Called after double down to prevent further actions on that hand.
     */
    public void disablePlayerActions() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        splitButton.setEnabled(false);
        doubleDownButton.setEnabled(false);
    }

    /**
     * Updates the Split button state based on game conditions.
     * Split is only enabled when:
     * - Round is active
     * - Player has exactly 2 cards in current hand
     * - Both cards have the same value (hand is splittable)
     * - Player has enough balance to place another bet of the same amount
     * - Hand hasn't been split yet
     */
    public void updateSplitButtonState() {
        if (!roundActive || game == null) {
            splitButton.setEnabled(false);
            return;
        }

        // Can't split if already split
        if (game.isSplitted()) {
            splitButton.setEnabled(false);
            return;
        }

        // Get current hand (only first hand can be split)
        if (game.getPlayer().getHands().isEmpty()) {
            splitButton.setEnabled(false);
            return;
        }

        Hand currentHand = game.getPlayer().getHands().get(0);

        // Check if hand is splittable (exactly 2 cards with same value)
        boolean isSplittable = currentHand != null && currentHand.splittable();

        // Check if player has enough balance to place another bet of the same amount
        int currentBet = (int) game.getBetAmount();
        boolean hasEnoughBalance = actualBalance >= currentBet;

        // Enable only if both conditions are met
        splitButton.setEnabled(isSplittable && hasEnoughBalance);
    }

    /**
     * Updates the Double Down button state based on game conditions.
     * Double Down is enabled when:
     * - Round is active
     * - Player has exactly 2 cards in current hand (hasn't hit yet)
     * - Player has enough balance to double the current bet
     * - If game is split, only enabled on split hand (not first hand after splitting)
     * It becomes disabled after:
     * - Player hits (now has more than 2 cards)
     * - Player splits and playing first hand (can only double down on split hand)
     * - Game ends
     * - Insufficient balance
     */
    public void updateDoubleDownButtonState() {
        if (!roundActive || game == null) {
            doubleDownButton.setEnabled(false);
            return;
        }

        // Get current hand
        Hand currentHand;
        if (game.isSplitted() && playingSplitHand) {
            // Playing split hand - can double down if conditions are met
            if (game.getPlayer().getHands().size() > 1) {
                currentHand = game.getPlayer().getHands().get(1);
            } else {
                doubleDownButton.setEnabled(false);
                return;
            }
        } else if (game.isSplitted() && !playingSplitHand) {
            // Game is split but playing first hand - disable double down after splitting
            doubleDownButton.setEnabled(false);
            return;
        } else {
            // Not split - playing first hand
            if (!game.getPlayer().getHands().isEmpty()) {
                currentHand = game.getPlayer().getHands().get(0);
            } else {
                doubleDownButton.setEnabled(false);
                return;
            }
        }

        // Check if hand has exactly 2 cards (double down requirement - disabled after hitting)
        boolean hasExactlyTwoCards = currentHand != null && currentHand.getCards().size() == 2;

        // Check if player has enough balance to double the bet
        int currentBet = (int) game.getBetAmount();
        boolean hasEnoughBalance = actualBalance >= currentBet;

        // Enable only if both conditions are met
        doubleDownButton.setEnabled(hasExactlyTwoCards && hasEnoughBalance);
    }

    /**
     * Gets the stand action listener (used by presenter to trigger stand after double down).
     * @return the stand action listener
     */
    public ActionListener getStandActionListener() {
        return standActionListener;
    }
    
    public JSpinner getBetSpinner() { return this.betSpinner; }
}
