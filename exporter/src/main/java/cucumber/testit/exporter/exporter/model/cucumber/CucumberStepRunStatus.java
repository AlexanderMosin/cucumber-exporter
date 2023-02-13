package cucumber.testit.exporter.exporter.model.cucumber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Represents possible step run statuses.
 */
@Getter
public enum CucumberStepRunStatus {

    @JsonProperty("failed")
    FAILED("Failed"),

    @JsonProperty("passed")
    PASSED("Passed"),

    @JsonProperty("skipped")
    SKIPPED("Skipped"),

    @JsonProperty("undefined")
    UNDEFINED("Undefined");

    /**
     * The value of step run status.
     */
    private final String value;

    CucumberStepRunStatus(String value) {
        this.value = value;
    }
}
