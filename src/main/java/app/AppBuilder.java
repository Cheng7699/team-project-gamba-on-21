package app;

import data_access.FileUserDataAccessObject;
import entity.AccountFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.launch.LaunchController;
import interface_adapter.launch.LaunchPresenter;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.launch.LaunchInputBoundary;
import use_case.launch.LaunchOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import view.BlackjackView;
import view.LoggedInView;
import view.LoginView;
import view.RulesView;
import view.SignupView;
import view.TopUpView;
import view.ViewManager;
import view.LaunchView;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final AccountFactory userFactory = new AccountFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using local file storage
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);

    // DAO version using a shared external database
    // final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(userFactory);

    private SignupView signupView;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private LaunchView launchView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;

    private TopUpView topUpView;
    private BlackjackView blackjackView;
    private RulesView rulesView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addLaunchView() {
        launchView = new LaunchView();
        cardPanel.add(launchView, launchView.getViewName());
        return this;
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel, viewManagerModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel, viewManagerModel, signupViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

//    public AppBuilder addTopUpView() {
//        topUpView = new TopUpView(loggedInViewModel, viewManagerModel);
//        cardPanel.add(topUpView, topUpView.getViewName());
//        return this;
//    }
//
    public AppBuilder addBlackjackView() {
        blackjackView = new BlackjackView(loggedInViewModel, viewManagerModel);
        cardPanel.add(blackjackView, blackjackView.getViewName());
        return this;
    }

//    public AppBuilder addRulesView() {
//        rulesView = new RulesView(loggedInViewModel, viewManagerModel);
//        cardPanel.add(rulesView, rulesView.getViewName());
//        return this;
//    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    /**
     * Adds the Launch Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLaunchUseCase() {
        final LaunchOutputBoundary launchOutputBoundary = new LaunchPresenter(viewManagerModel,
                loginViewModel, signupViewModel);

        final LaunchInputBoundary launchInteractor =
                new use_case.launch.LaunchInteractor(launchOutputBoundary);

        final LaunchController launchController = new LaunchController(launchInteractor);
        launchView.setLaunchController(launchController);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("GAMBLIN' TIME");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(launchView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }


}
