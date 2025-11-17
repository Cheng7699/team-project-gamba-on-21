package use_case.change_password;

import entity.Accounts;
import entity.AccountFactory;

/**
 * The Change Password Interactor.
 */
public class ChangePasswordInteractor implements ChangePasswordInputBoundary {
    private final ChangePasswordUserDataAccessInterface userDataAccessObject;
    private final ChangePasswordOutputBoundary userPresenter;
    private final AccountFactory accountFactory;

    public ChangePasswordInteractor(ChangePasswordUserDataAccessInterface changePasswordDataAccessInterface,
                                    ChangePasswordOutputBoundary changePasswordOutputBoundary,
                                    AccountFactory accountFactory) {
        this.userDataAccessObject = changePasswordDataAccessInterface;
        this.userPresenter = changePasswordOutputBoundary;
        this.accountFactory = accountFactory;
    }

    @Override
    public void execute(ChangePasswordInputData changePasswordInputData) {
        if ("".equals(changePasswordInputData.getPassword())) {
            userPresenter.prepareFailView("New password cannot be empty");
            return;
        }

        final Accounts account = accountFactory.create(
                changePasswordInputData.getUsername(),
                changePasswordInputData.getPassword()
        );

        userDataAccessObject.changePassword(account);

        final ChangePasswordOutputData changePasswordOutputData =
                new ChangePasswordOutputData(account.getUsername());

        userPresenter.prepareSuccessView(changePasswordOutputData);
    }
}
