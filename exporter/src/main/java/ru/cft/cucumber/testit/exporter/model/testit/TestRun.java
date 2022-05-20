package ru.cft.cucumber.testit.exporter.model.testit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents an entity for creating TestIt test run.
 */
@Getter
@Setter
@Builder(builderClassName = "Builder", toBuilder = true)
public class TestRun {

    /**
     * The identifier of TestIt project.
     */
    private String projectId;

    /**
     * The name of test run.
     */
    private String name;

    /**
     * The identifier of configuration.
     */
    private List<String> configurationIds;

    /**
     * The external identifier of autotest.
     */
    private List<String> autoTestExternalIds;

    /**
     * The description of test run.
     */
    private String description;

    /**
     * The launch source of test run.
     */
    private String launchSource;
}
