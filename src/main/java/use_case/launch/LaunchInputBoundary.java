package use_case.launch;

/**
 * Input Boundary for actions which are related to the launch screen in.
 */
public interface LaunchInputBoundary {

        /**
         * Executes the launch (screen) use case.
         *
         */
        void execute(LaunchInputData launchInputData);
}
