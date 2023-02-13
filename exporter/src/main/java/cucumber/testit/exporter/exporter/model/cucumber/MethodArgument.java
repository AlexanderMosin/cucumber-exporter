package cucumber.testit.exporter.exporter.model.cucumber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an argument of method.
 */
@Getter
@Setter
public class MethodArgument {

    /**
     * The value of argument.
     */
    @JsonProperty("val")
    private String value;
}