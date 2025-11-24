package interface_adapter.launch;

import interface_adapter.ViewModel;

public class LaunchViewModel extends ViewModel<LaunchState> {

    public LaunchViewModel() {
        super("log in");
        setState(new LaunchState());
    }
}
