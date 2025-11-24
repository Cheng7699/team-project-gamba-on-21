package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.launch.LaunchController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

import framework.RoundedButton;
import interface_adapter.launch.LaunchViewModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupViewModel;


public class LaunchView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "launch";
    private final String imagePath = "../../resources/poker-image.jpg";
    private BufferedImage backgroundImage;


    private LaunchController launchController = null;
    LaunchViewModel launchViewModel;
    ViewManagerModel viewManagerModel;
    SignupViewModel signupViewModel;
    LoginViewModel loginViewModel;

    private final RoundedButton toSignUpButton;
    private final RoundedButton toLoginButton;

    private final int buttonRadius = 100;
    private final int buttonHeight = 5;
    private final int buttonWidth = 5;

    public LaunchView(LaunchViewModel launchViewModel,
                      ViewManagerModel viewManagerModel,
                      SignupViewModel signupViewModel,
                      LoginViewModel loginViewModel) {

        this.launchViewModel = launchViewModel;
        this.viewManagerModel = viewManagerModel;
        this.signupViewModel = signupViewModel;
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        //Choose the layout
        setLayout(new GridBagLayout());

        // load image from classpath
        try {
            // put your image in src/main/resources/resources/poker-image.jpg
            backgroundImage = ImageIO.read(getClass().getResource("/launch-bg.jpg"));
        } catch (IOException | IllegalArgumentException ex) {
            // IllegalArgumentException if resource not found
            System.err.println("Could not load background image: " + ex.getMessage());
            backgroundImage = null;
        }

        toLoginButton = new RoundedButton("Login", buttonRadius);
        toSignUpButton = new RoundedButton("Sign Up", buttonRadius);
        toLoginButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { launchController.SwitchToLogin();}
                }
        );
        toSignUpButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { launchController.SwitchToSignUp();}
                }
        );

        // Choose font sizes
        try {
            // Load the font from a file
            URL fontUrl = getClass().getResource("/Italiana-Regular.ttf");
            Font italianaFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream()).deriveFont(50f);
            toLoginButton.setFont(italianaFont);
            toSignUpButton.setFont(italianaFont);
        } catch (FontFormatException | IOException e) {
            toLoginButton.setFont(toSignUpButton.getFont().deriveFont(20f));
            toSignUpButton.setFont(toSignUpButton.getFont().deriveFont(20f));
        }


        GridBagConstraints gbc;

        // 1) Top spacer to push content to bottom
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;         // span all three columns
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;        // takes all vertical free space
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createGlue(), gbc);

        // --- First button row (Login) ---
        // left flexible spacer (about 70% of width)
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.7;        // 70% of remaining horizontal weight
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createGlue(), gbc);

        // button column (about 30% of width)
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;        // 30% of remaining horizontal weight
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH; // fill only that column horizontally
        gbc.insets = new Insets(0, 0, 6, 16); // small gap and right margin
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        add(toLoginButton, gbc);

        // tiny right spacer (keeps grid consistent)
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createRigidArea(new Dimension(0, 0)), gbc);

        // --- Space row between buttons ---
        gbc = new  GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.05;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createGlue(), gbc);

        // --- Second button row (Sign Up) ---
        // left flexible spacer (same as above)
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.65;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createGlue(), gbc);

        // button column (same 30%)
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 16, 16); // bottom margin & right margin
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        add(toSignUpButton, gbc);

        // right spacer for the second row
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.05;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createRigidArea(new Dimension(0, 0)), gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2 = (Graphics2D) g.create();

            // optional: high-quality scaling
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        } else {
            // fallback if image missing
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("Background image not found", 10, 20);
        }
    }

    public void setLaunchController(LaunchController launchController) { this.launchController = launchController; }


    @Override
    public void actionPerformed(ActionEvent evt) {
        if (launchController == null) {
            return;
        }
        if (evt.getSource() == toLoginButton) {
            launchController.SwitchToLogin();
        } else if (evt.getSource() == toSignUpButton) {
            launchController.SwitchToSignUp();
        }
    }

@Override
    public void propertyChange(PropertyChangeEvent evt) { JOptionPane.showMessageDialog(this, "Cancel not implemented yet."); }

    public String getViewName() {
        return viewName;
    }
}


