package ru.cft.cucumber.testit.exporter.model.testit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents an entity for sending test run results.
 */
@Getter
@Setter
@Builder(builderClassName = "Builder", toBuilder = true)
public class TestRunResult {

    /**
     * The identifier of configuration.
     */
    private String configurationId;

    /**
     * The external identifier of autotest.
     */
    private String autoTestExternalId;

    /**
     * The outcome of test.
     */
    private String outcome;

    /**
     * Results of steps including in the test.
     */
    private List<TestStepResult> stepResults;
}
