package cucumber.testit.exporter.exporter.model.testit;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Contains information about test run and corresponding results.
 */
@Getter
@Setter
public class TestRunData {

    /**
     * The information about test run.
     */
    private TestRun testRun;

    /**
     * The results corresponding to the test run.
     */
    private List<TestRunResult> testRunResults;
}