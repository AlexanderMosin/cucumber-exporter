package ru.cft.cucumber.testit.exporter.model.cucumber;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a cucumber scenario.
 */
@Getter
@Setter
public class CucumberScenario {

    /**
     * Elements including in the scenario.
     */
    private List<CucumberElement> elements;
}
