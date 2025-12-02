package app;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        final int applicationWidth = 1280;
        final int applicationHeight = 720;

        final AppBuilder appBuilder = new AppBuilder();
        final JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLaunchView()
                .addLaunchUseCase()
                .addLoggedInView()
                .addTopUpView()
                .addTopupUseCase()
                .addBlackjackView()
                .addRulesView()
                .addLaunchUseCase()
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .addGameStartUseCase()
                .addPlayerSplitUseCase()
                .addPlayerHitUseCase()
                .addPlayerDoubleDownUseCase()
                .addPlayerStandUseCase()
                .build();

        application.pack();
        application.setSize(applicationWidth, applicationHeight);
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
