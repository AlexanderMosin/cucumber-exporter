package cucumber.testit.exporter.exporter.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.testit.exporter.exporter.ExporterException;

/**
 * An auxiliary class used for serialization an object to JSON.
 */
public final class JsonSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private JsonSerializer() {
    }

    public static String serialize(Object object) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            throw new ExporterException("Unable to write value as json", e);
        }
    }
}
