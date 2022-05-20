package ru.cft.cucumber.testit.exporter.model.cucumber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Represents possible test statuses.
 */
@Getter
public enum CucumberTestStatus {

    @JsonProperty("failed")
    FAILED("Failed"),

    @JsonProperty("passed")
    PASSED("Passed"),

    @JsonProperty("skipped")
    SKIPPED("Skipped"),

    @JsonProperty("undefined")
    UNDEFINED("Undefined");

    /**
     * The value of test status.
     */
    private final String value;

    CucumberTestStatus(String value) {
        this.value = value;
    }
}
