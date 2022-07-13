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
     * The external identifier of autotest.
     */
    private String externalId;

    /**
     * The test was deleted or not.
     */
    private boolean isDeleted;
}
