package ru.cft.cucumber.testit.exporter.model.cucumber;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a cucumber step.
 */
@Getter
@Setter
public class CucumberStep {

    /**
     * The name of step.
     */
    private String name;

    /**
     * The result of step.
     */
    private CucumberTestResult result;
}

