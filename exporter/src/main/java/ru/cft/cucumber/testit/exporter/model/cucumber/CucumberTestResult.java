package ru.cft.cucumber.testit.exporter.model.cucumber;

import lombok.Getter;

/**
 * Represents a cucumber test result.
 */
@Getter
public class CucumberTestResult {

    /**
     * The status of result.
     */
    private CucumberTestStatus status;
}
