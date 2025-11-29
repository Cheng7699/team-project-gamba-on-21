package interface_adapter.topup;

import interface_adapter.ViewModel;
import interface_adapter.signup.SignupState;

public class TopupViewModel extends ViewModel<TopUpState> {

    public static final String TITLE_LABEL = "Top-Up Account";
    //public static final String USERNAME_LABEL = "Account username";
    public static final String TOPUP_LABEL = "Amount to Top-Up";

    public static final String TOPUP_BUTTON_LABEL = "Top up";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    public TopupViewModel() {
        super("top up");
        setState(new TopUpState());
    }



}
