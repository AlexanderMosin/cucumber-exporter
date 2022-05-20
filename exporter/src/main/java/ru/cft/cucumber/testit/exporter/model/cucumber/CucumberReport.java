package ru.cft.cucumber.testit.exporter.model.cucumber;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a cucumber report.
 */
@Getter
@Setter
public class CucumberReport {

    /**
     * Scenarios including in the report.
     */
    private List<CucumberScenario> scenarios;

    public CucumberReport(List<CucumberScenario> scenarios) {
        this.scenarios = scenarios;
    }
}
