package cucumber.testit.exporter.exporter.model.testit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents TestIt autotest step result.
 */
@Getter
@Setter
@AllArgsConstructor
public class TestStepResult {

    /**
     * The title of step.
     */
    private String title;

    /**
     * The outcome of step.
     */
    private String outcome;
}
