package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLaunchView()
                .addLaunchUseCase()
                .addLoggedInView()
                .addTopUpView()
                .addTopupUseCase()
                .addBlackjackView()
//                .addRulesView()
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
        application.setSize(1280, 720);
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
