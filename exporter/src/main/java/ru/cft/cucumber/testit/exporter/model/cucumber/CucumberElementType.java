package ru.cft.cucumber.testit.exporter.model.cucumber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Represents possible element types.
 */
@Getter
public enum CucumberElementType {

    @JsonProperty("scenario")
    SCENARIO("scenario"),

    @JsonProperty("background")
    BACKGROUND("background");

    /**
     * The value of type.
     */
    private final String value;

    CucumberElementType(String value) {
        this.value = value;
    }
}
