package ru.cft.cucumber.testit.exporter.model.cucumber;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an element of cucumber report.
 */
@Getter
@Setter
public class CucumberElement {

    /**
     * The name of element.
     */
    private String name;

    /**
     * Steps including in the element.
     */
    private List<CucumberStep> steps;

    /**
     * Scenario tags
     */
    private List<CucumberTag> tags;

    /**
     * The type of cucumber element: scenario or background
     */
    private CucumberElementType type;
}
