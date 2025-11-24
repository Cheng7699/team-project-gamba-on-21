package view;

import entity.Card;
import entity.Hand;
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

    private final JLabel dealerHandLabel = new JLabel("Dealer: -");
    private final JLabel playerHandLabel = new JLabel("Player: -");

    private final JButton hitButton = new JButton("Hit");
    private final JButton standButton = new JButton("Stand");
    private final JButton rulesButton = new JButton("Rules");
    private final JButton quitButton = new JButton("Quit");
    private final JButton placeBetButton = new JButton("Place Bet");
    private final JButton newRoundButton = new JButton("New Round");

    private Hand playerHand;
    private Hand dealerHand;
    private boolean hideDealerHoleCard;
    private boolean betLocked;
    private boolean roundActive;

    private ActionListener hitActionListener;
    private ActionListener standActionListener;
    private ActionListener newRoundActionListener;
    private ActionListener placeBetActionListener;

    public BlackjackView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

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
        rulesButton.addActionListener(this);
        quitButton.addActionListener(this);
        placeBetButton.addActionListener(this);
        newRoundButton.addActionListener(this);

        betSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                betValueLabel.setText("$" + betSpinner.getValue());
            }
        });

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
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
        }
        else if (source.equals(newRoundButton)) {
            resetRound();
            if (newRoundActionListener != null) {
                newRoundActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "newRound"));
            }
        }
        else if (source.equals(hitButton)) {
            playerHits();
        }
        else if (source.equals(standButton)) {
            playerStands();
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
            balanceValueLabel.setText("$" + state.getBalance());
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

        betLocked = true;
        betSpinner.setEnabled(false);
        placeBetButton.setEnabled(false);
        betValueLabel.setText("$" + selectedBet);

        if (placeBetActionListener != null) {
            placeBetActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "placeBet"));
        }

        startRound();
    }

    private void startRound() {
        roundActive = true;
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
        newRoundButton.setEnabled(false);

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
        betValueLabel.setText("$" + betSpinner.getValue());
        playerHand = null;
        dealerHand = null;
        hideDealerHoleCard = false;
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
            hitActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "hit"));
        }
    }

    private void playerStands() {
        if (!roundActive) {
            statusLabel.setText("Start a round by placing a bet first.");
            return;
        }

        statusLabel.setText("Stand chosen. Waiting for dealer...");
        if (standActionListener != null) {
            standActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "stand"));
        }
    }

    private void endRound(String message) {
        roundActive = false;
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        newRoundButton.setEnabled(true);
        updateHandLabels(false);
        statusLabel.setText(message + " Click New Round to place another bet.");
    }

    private void updateHandLabels(boolean hideDealerHoleCard) {
        dealerHandLabel.setText(formatHandLabel("Dealer", dealerHand, hideDealerHoleCard));
        playerHandLabel.setText(formatHandLabel("Player", playerHand, false));
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
        this.dealerHand = dealerHand;
        this.hideDealerHoleCard = hideDealerHoleCard;
        updateHandLabels(hideDealerHoleCard);
    }

    public Hand getDealerHand() { return dealerHand; }
    public boolean isHideDealerHoleCard() { return hideDealerHoleCard; }

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

    public void setNewRoundActionListener(ActionListener newRoundActionListener) {
        this.newRoundActionListener = newRoundActionListener;
    }

    public void setPlaceBetActionListener(ActionListener placeBetActionListener) {
        this.placeBetActionListener = placeBetActionListener;
    }
}
