package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logging into the program.
 */
public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "log in";
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;
    private final SignupViewModel signupViewModel;

    private final JTextField usernameInputField = new JTextField(15);
    private final JLabel usernameErrorField = new JLabel();

    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JLabel passwordErrorField = new JLabel();

    private final JButton logIn;
    private final JButton cancel;
    private LoginController loginController = null;
    private JDialog currentErrorDialog = null;

    public LoginView(LoginViewModel loginViewModel, ViewManagerModel viewManagerModel, SignupViewModel signupViewModel) {

        this.loginViewModel = loginViewModel;
        this.viewManagerModel = viewManagerModel;
        this.signupViewModel = signupViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Login Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel usernameInfo = new LabelTextPanel(
                new JLabel("Username"), usernameInputField);
        final LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        final JPanel buttons = new JPanel();
        logIn = new JButton("Log In");
        buttons.add(logIn);
        cancel = new JButton("Cancel");
        buttons.add(cancel);

        logIn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(logIn)) {
                            // read directly from input fields to ensure we get current values
                            // this avoids any potential state synchronization issues
                            final String username = usernameInputField.getText();
                            final String password = new String(passwordInputField.getPassword());

                            loginController.execute(username, password);
                        }
                    }
                }
        );

        cancel.addActionListener(this);

        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                loginViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setPassword(new String(passwordInputField.getPassword()));
                loginViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        // Create a container panel with BoxLayout for the content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(title);
        contentPanel.add(usernameInfo);
        contentPanel.add(usernameErrorField);
        contentPanel.add(passwordInfo);
        contentPanel.add(buttons);
        
        // Use GridBagLayout to center the content
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(contentPanel, gbc);
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(cancel)) {
            // switch back to launch view
            viewManagerModel.setState("launch");
            viewManagerModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoginState state = (LoginState) evt.getNewValue();
        setFields(state);
        usernameErrorField.setText(state.getLoginError());

        // login failed popup
        if (state.getLoginError() != null && !state.getLoginError().isEmpty()) {
            showErrorPopup(state.getLoginError());
        }
    }
    
    /**
     * Shows an error popup with try again button.
     * @param errorMessage the error message to display
     */
    private void showErrorPopup(String errorMessage) {
        // Don't show multiple popups at once
        if (currentErrorDialog != null && currentErrorDialog.isVisible()) {
            return;
        }
        
        JDialog errorDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Login Error", true);
        currentErrorDialog = errorDialog;
        errorDialog.setLayout(new BorderLayout());
        errorDialog.setSize(300, 150);
        errorDialog.setLocationRelativeTo(this);
        
        // error message
        JLabel errorLabel = new JLabel("Incorrect Username / Password");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        errorDialog.add(errorLabel, BorderLayout.CENTER);

        // try again button
        JButton tryAgainButton = new JButton("Try Again");
        tryAgainButton.addActionListener(e -> {
            errorDialog.dispose();
            currentErrorDialog = null;
            // Clear the error from state
            LoginState currentState = loginViewModel.getState();
            currentState.setLoginError(null);
            loginViewModel.setState(currentState);
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(tryAgainButton);
        errorDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        errorDialog.setVisible(true);
    }

    private void setFields(LoginState state) {
        usernameInputField.setText(state.getUsername());
        // only clear password field if state password is empty (like after logout)
        // otherwise, let DocumentListener handle updates to avoid interfering with user input
        if (state.getPassword() == null || state.getPassword().isEmpty()) {
            passwordInputField.setText("");
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
