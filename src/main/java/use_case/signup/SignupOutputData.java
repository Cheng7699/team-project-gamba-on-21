package use_case.signup;

/**
 * Output Data for the Signup Use Case.
 */
public class SignupOutputData {

    private final String username;

    public SignupOutputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
