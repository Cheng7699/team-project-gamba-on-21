package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RulesView extends JPanel implements ActionListener{
    public static final String VIEW_NAME = "rules";

    private final ViewManagerModel viewManagerModel;

    private final JButton backButton = new JButton("Back");
    private final JTextField ruleName = new JTextField();

    private interface_adapter.readrule.ReadRuleController readRuleController;

    public RulesView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setAlignmentX(CENTER_ALIGNMENT);

        final JLabel titleLabel = new JLabel("Rules");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        final JLabel textLabel = getJLabel();
        textLabel.setLayout(new BoxLayout(textLabel, BoxLayout.Y_AXIS));
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        final JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(textLabel);
        add(centerPanel, BorderLayout.CENTER);

        final JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        optionsPanel.add(backButton);

        add(optionsPanel, BorderLayout.SOUTH);

        backButton.addActionListener(this);
    }

    @NotNull
    private static JLabel getJLabel() {
        final JLabel textLabel= new JLabel();
        textLabel.setText("<html>" +
                "<b>Blackjack Rules and Odds:</b><br>" +
                "<ul>" +
                "<li>Game uses one player and one dealer with a standard 52-card deck.</li>" +
                "<li>Cards keep face value; face cards = 10, Aces = 1 or 11.</li>" +
                "<li>Both player and dealer start with two cards.</li>" +
                "<li>Player cards are face up; dealer shows one and hides one.</li>" +
                "<li>Goal: reach a total as close to 21 as possible without going over.</li>" +
                "<li>Player may Hit or Stand.</li>" +
                "<li>Dealer must hit until 17, then stand.</li>" +
                "<li>Blackjack beats all hands except dealer Blackjack (push).</li>" +
                "<li>Player bust over 21 loses; dealer bust gives player win.</li>" +
                "<li>Odds: Player wins ~42–44% of hands.</li>" +
                "<li>Dealer wins ~48–49% of hands.</li>" +
                "<li>Pushes occur ~8–9% of the time.</li>" +
                "<li>Chance of being dealt Blackjack: ~4.83%.</li>" +
                "</ul>" +
                "</html>");
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        return textLabel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        if (source.equals(backButton)) {
            navigateTo(viewManagerModel.getPreviousState());
        }
    }

    private void navigateTo(String destinationView) {
        viewManagerModel.setState(destinationView);
        viewManagerModel.setPreviousState(null);
        viewManagerModel.firePropertyChange();
    }

    public void getRules() {
        readRuleController.readRule();
    }

    public void setRuleName(String ruleName) {
        this.ruleName.setText(ruleName);
    }

    public String getViewName(){
        return VIEW_NAME;
    }

    public JTextField getRuleName() {
        return ruleName;
    }
}

