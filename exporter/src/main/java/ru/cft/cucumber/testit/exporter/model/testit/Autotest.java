package ru.cft.cucumber.testit.exporter.model.testit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents TestIt autotest entity.
 */
@Getter
@Setter
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = Autotest.Builder.class)
public class Autotest {

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
    }

    /**
     * External identifier of autotest in TestIt
     */
    private String externalId;

    /**
     * Global identifier of object in TestIt
     */
    private String globalId;

    /**
     * Identifier of TestIt project.
     */
    private String projectId;

    /**
     * Name of autotest in TestIt
     */
    private String name;

    /**
     * Title of autotest in TestIt
     */
    private String title;

    /**
     * Name of a group or package where this autotest's class is located
     */
    private String namespace;

    /**
     * Name of a group or package where this autotest's class is located
     */
    private String classname;

    /**
     * Should create workitem when autotest was added
     */
    private boolean shouldCreateWorkItem;

    /**
     * The test was deleted or not.
     */
    private boolean isDeleted;
}
