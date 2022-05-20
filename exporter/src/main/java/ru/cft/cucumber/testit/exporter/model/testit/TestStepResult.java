package ru.cft.cucumber.testit.exporter.model.testit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents TestIt autotest step result.
 */
@Getter
@Setter
@Builder(builderClassName = "Builder", toBuilder = true)
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
