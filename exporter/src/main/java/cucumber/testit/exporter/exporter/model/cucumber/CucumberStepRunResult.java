package cucumber.testit.exporter.exporter.model.cucumber;

import lombok.Getter;

/**
 * Represents a cucumber step run result.
 */
@Getter
public class CucumberStepRunResult {

    /**
     * The status of step run result.
     */
    private CucumberStepRunStatus status;
}
