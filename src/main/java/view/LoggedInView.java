package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.topup.TopUpController;
import interface_adapter.topup.TopUpState;
import interface_adapter.topup.TopupViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logged into the program
 */
public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = LoggedInViewModel.VIEW_NAME;
    private final LoggedInViewModel loggedInViewModel;
    private final TopupViewModel topupViewModel;
    private final ViewManagerModel viewManagerModel;
    private ChangePasswordController changePasswordController = null;
    private LogoutController logoutController;

    private final JLabel welcomeLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();

    private final JButton logOutButton = new JButton("Log Out");
    private final JButton topUpButton = new JButton("Top Up");
    private final JButton playButton = new JButton("Play");
    private final JButton rulesButton = new JButton("Read Rules");

    public LoggedInView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel,
                        TopupViewModel topupViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel.addPropertyChangeListener(this);
        this.topupViewModel = topupViewModel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        final JLabel title = new JLabel("Welcome to BlackJack");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(welcomeLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(balanceLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(statusLabel);

        final JPanel actionsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        playButton.addActionListener(this);
        rulesButton.addActionListener(this);
        topUpButton.addActionListener(this);
        logOutButton.addActionListener(this);
        actionsPanel.add(playButton);
        actionsPanel.add(rulesButton);
        actionsPanel.add(topUpButton);
        actionsPanel.add(logOutButton);

        add(title);
        add(Box.createVerticalStrut(12));
        add(infoPanel);
        add(Box.createVerticalStrut(12));
        add(actionsPanel);

        final LoggedInState initialState = loggedInViewModel.getState();
        if (initialState != null) {
            welcomeLabel.setText("Logged in as: " + initialState.getUsername());
            updateBalance(initialState);
            statusLabel.setText(initialState.getStatusMessage());
        }
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(logOutButton)) {
            if (logoutController != null) {
                this.logoutController.execute();
            }
        }
        else if (evt.getSource().equals(topUpButton)) {
            String username = loggedInViewModel.getState().getUsername();

            TopUpState topupState = topupViewModel.getState();

            topupState.setUsername(username);
            topupState.setTopupAmount("");
            topupState.setTopupAmountError(null);

            topupViewModel.setState(topupState);
            topupViewModel.firePropertyChange();

            navigateTo(TopUpView.VIEW_NAME);

        }
        else if (evt.getSource().equals(playButton)) {
            navigateTo(BlackjackView.VIEW_NAME);
        }
        else if (evt.getSource().equals(rulesButton)) {
            navigateTo("rules");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            welcomeLabel.setText("Logged in as: " + state.getUsername());
            updateBalance(state);
            statusLabel.setText(state.getStatusMessage());
        }
        else if (evt.getPropertyName().equals("balance")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            updateBalance(state);
        }
        else if (evt.getPropertyName().equals("message")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            statusLabel.setText(state.getStatusMessage());
        }

    }

    private void updateBalance(LoggedInState state) {
        if (state == null) {
            return;
        }
        balanceLabel.setText("Current balance: $" + state.getBalance());
    }

    private void navigateTo(String viewName) {
        viewManagerModel.setState(viewName);
        viewManagerModel.setPreviousState(this.viewName);
        viewManagerModel.firePropertyChange();
    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }
}
