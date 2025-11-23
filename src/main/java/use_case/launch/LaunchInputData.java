package use_case.launch;

public class LaunchInputData {

    private final String destinationPage;

    public LaunchInputData(String destinationPage) {
        this.destinationPage = destinationPage;
    }

    String getDestinationPage() { return destinationPage; }
}
