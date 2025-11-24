package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLaunchView()
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
//                .addTopUpView()
                .addBlackjackView()
//                .addRulesView()
                .addLaunchUseCase()
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .build();

        application.pack();
        application.setSize(1280, 720);
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
