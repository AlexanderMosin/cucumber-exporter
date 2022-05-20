package ru.cft.cucumber.testit.exporter.json;

import java.io.File;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.cft.cucumber.testit.exporter.ExporterException;

/**
 * An auxiliary class used for deserialization the JSON to object.
 */
public final class JsonDeserializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false
    );

    private static final String FAILED_TO_DESERIALIZE_MESSAGE = "Failed to deserialize JSON data";

    private JsonDeserializer() {
    }

    public static <T> T deserialize(File file, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(file, typeReference);
        } catch (Exception e) {
            throw new ExporterException(FAILED_TO_DESERIALIZE_MESSAGE, e);
        }
    }

    public static <T> T deserialize(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new ExporterException(FAILED_TO_DESERIALIZE_MESSAGE, e);
        }
    }
}
