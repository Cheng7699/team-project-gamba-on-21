package app;

import data_access.FileUserDataAccessObject;
import data_access.DeckApiClient;
import data_access.PlayerHitDataAccess;
import entity.*;
import interface_adapter.PlayerStand.PlayerStandController;
import interface_adapter.PlayerStand.PlayerStandPresenter;
import interface_adapter.payout.PayoutController;
import interface_adapter.payout.PayoutPresenter;
import interface_adapter.placeBet.PlaceBetController;
import interface_adapter.ViewManagerModel;
import interface_adapter.game_start.GameStartController;
import interface_adapter.game_start.GameStartPresenter;
import interface_adapter.launch.LaunchController;
import interface_adapter.launch.LaunchPresenter;
import interface_adapter.launch.LaunchViewModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.playerHit.PlayerHitController;
import interface_adapter.playerHit.PlayerHitPresenter;
import interface_adapter.playerSplit.PlayerSplitController;
import interface_adapter.playerSplit.PlayerSplitPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.topup.TopUpController;
import interface_adapter.topup.TopUpPresenter;
import interface_adapter.topup.TopupViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.game_start.GameStartDataAccessInterface;
import use_case.game_start.GameStartInputBoundary;
import use_case.game_start.GameStartInteractor;
import use_case.game_start.GameStartOutputBoundary;
import use_case.launch.LaunchInputBoundary;
import use_case.launch.LaunchInteractor;
import use_case.launch.LaunchOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.playerHit.PlayerHitInputBoundary;
import use_case.playerHit.PlayerHitInteractor;
import use_case.playerHit.PlayerHitOutputBoundary;
import use_case.playerHit.PlayerHitUserDataAccessInterface;
import use_case.playerSplit.PlayerSplitDataAccessInterface;
import use_case.playerSplit.PlayerSplitInputBoundary;
import use_case.playerSplit.PlayerSplitInteractor;
import use_case.playerSplit.PlayerSplitOutputBoundary;
import use_case.payout.PayoutInteractor;
import use_case.payout.PayoutOutputBoundary;
import use_case.placeBet.PlaceBetInteractor;
import use_case.playerStand.PlayerStandInputBoundary;
import use_case.playerStand.PlayerStandInteractor;
import use_case.playerStand.PlayerStandOutputBoundary;
import use_case.playerStand.PlayerStandUserDataAccessInterface;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.topup.TopupInputBoundary;
import use_case.topup.TopupInputData;
import use_case.topup.TopupInteractor;
import use_case.topup.TopupOutputBoundary;
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
    private LaunchViewModel launchViewModel;
    private TopupViewModel topupViewModel;

    private TopUpView topUpView;
    private BlackjackView blackjackView;
    private RulesView rulesView;

    private BlackjackGame blackjackGame = new BlackjackGame("", null, null);
    private DeckApiClient deckApiClient = new DeckApiClient();

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
        topupViewModel = new TopupViewModel();
    }

    public AppBuilder addLaunchView() {
        launchViewModel = new LaunchViewModel();
        launchView = new LaunchView(launchViewModel, viewManagerModel, signupViewModel, loginViewModel);
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
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel, topupViewModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addTopUpView() {
        topUpView = new TopUpView(topupViewModel, viewManagerModel);
        cardPanel.add(topUpView, topUpView.getViewName());
        return this;
    }

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

    public AppBuilder addLaunchUseCase() {
        final LaunchOutputBoundary launchOutputBoundary = new LaunchPresenter(viewManagerModel,
                loginViewModel, signupViewModel, launchViewModel);
        final LaunchInputBoundary launchInteractor = new LaunchInteractor(launchOutputBoundary);

        LaunchController launchController = new LaunchController(launchInteractor);
        launchView.setLaunchController(launchController);
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

    public AppBuilder addTopupUseCase() {
        final TopupOutputBoundary topupOutputBoundary = new TopUpPresenter(topupViewModel,
                loggedInViewModel, viewManagerModel);

        final TopupInputBoundary topupInteractor =
                new TopupInteractor(userDataAccessObject, topupOutputBoundary, userFactory);

        TopUpController topupController = new TopUpController(topupInteractor);
        topUpView.setTopupController(topupController);
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

    public AppBuilder addGameStartUseCase() {
        GameStartDataAccessInterface apiClient = new DeckApiClient();

        GameStartOutputBoundary presenter = new GameStartPresenter(blackjackView);

        blackjackGame = new BlackjackGame("",
                new BlackjackDealer(),
                new BlackjackPlayer(loggedInViewModel.getState().getUsername()));

        GameStartInputBoundary interactor = new GameStartInteractor(blackjackGame, apiClient, presenter);

        GameStartController controller = new GameStartController(interactor);

        blackjackView.setGameStartActionListener(e -> {
            controller.gameStart(blackjackGame);
        });
        
        // wire up place bet use case to deduct bet from balance
        PlaceBetInteractor placeBetInteractor = new PlaceBetInteractor(userDataAccessObject);
        PlaceBetController placeBetController = new PlaceBetController(placeBetInteractor);
        blackjackView.setPlaceBetActionListener(e -> {
            // get bet amount from view and deduct it
            int betAmount = (Integer) blackjackView.getBetSpinner().getValue();
            placeBetController.execute(betAmount);
            // update balance in view model (interactor already updated the account)
            Accounts account = userDataAccessObject.get(userDataAccessObject.getCurrentUsername());
            if (account != null) {
                LoggedInState state = loggedInViewModel.getState();
                state.setBalance(account.getBalance());
                loggedInViewModel.setState(state);
                loggedInViewModel.firePropertyChange("balance");
            }
        });
        
        return this;
    }

    public AppBuilder addPlayerStandUseCase() {

        PlayerStandUserDataAccessInterface apiClient = new DeckApiClient();

        PlayerStandPresenter presenter = new PlayerStandPresenter(blackjackView);

        PlayerStandInputBoundary interactor = new PlayerStandInteractor(blackjackGame, apiClient, presenter);

        PlayerStandController controller = new PlayerStandController(interactor);

        blackjackView.setStandActionListener(e -> {
            controller.stand(blackjackGame);
        });

        // wire up payout use case
        PayoutOutputBoundary payoutPresenter = new PayoutPresenter(loggedInViewModel);
        PayoutInteractor payoutInteractor = new PayoutInteractor(userDataAccessObject, payoutPresenter);
        PayoutController payoutController = new PayoutController(payoutInteractor);
        presenter.setPayoutController(payoutController);

        return this;
    }

    public AppBuilder addPlayerSplitUseCase() {

        PlayerSplitDataAccessInterface deckAccess = new DeckApiClient();

        PlayerSplitOutputBoundary presenter = new PlayerSplitPresenter(blackjackView);

        PlayerSplitInputBoundary interactor = new PlayerSplitInteractor(blackjackGame, deckAccess, presenter);

        PlayerSplitController controller = new PlayerSplitController(interactor);

        blackjackView.setSplitActionListener(e -> controller.split(blackjackGame));

        return this;
    }


    public AppBuilder addPlayerHitUseCase() {

        PlayerHitUserDataAccessInterface deckAccess = new PlayerHitDataAccess(deckApiClient, blackjackGame);

        PlayerHitPresenter presenter = new PlayerHitPresenter(blackjackView);

        PlayerHitInputBoundary interactor = new PlayerHitInteractor(deckAccess, presenter);

        PlayerHitController controller = new PlayerHitController(interactor);

        blackjackView.setHitActionListener(e -> {
            boolean isSplitHand = "hitSplit".equals(e.getActionCommand());
            controller.hit(blackjackGame.getPlayer(), isSplitHand);
        });

        // wire up payout use case for bust handling
        PayoutOutputBoundary payoutPresenter = new PayoutPresenter(loggedInViewModel);
        PayoutInteractor payoutInteractor = new PayoutInteractor(userDataAccessObject, payoutPresenter);
        PayoutController payoutController = new PayoutController(payoutInteractor);
        presenter.setPayoutController(payoutController);

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
