package ru.cft.cucumber.testit.exporter.model.cucumber;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Contains information about method matching a step of Cucumber scenario.
 */
@Getter
@Setter
public class MethodMatch {

    /**
     * Arguments including in the match.
     */
    private List<MethodArgument> arguments;
}