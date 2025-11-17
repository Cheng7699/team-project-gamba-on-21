package use_case.topup;

/**
 * Output Data for the Topup Use case (the username of the accout that got topped up)
 */
public class TopupOutputData {

    private final String username;

    public TopupOutputData(String username){this.username=username;}

    public String getUsername(){return username;}
}
