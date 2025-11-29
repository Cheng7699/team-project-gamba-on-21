package use_case.topup;

/**
 * Output Data for the Topup Use case (the username of the accout that got topped up)
 */
public class TopupOutputData {

    private final int newBalance;

    public TopupOutputData(int newBalance){this.newBalance=newBalance;}

    public int getnewBalance(){return newBalance;}
}
