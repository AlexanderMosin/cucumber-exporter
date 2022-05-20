package ru.cft.cucumber.testit.exporter.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ru.cft.cucumber.testit.exporter.ExporterException;

import java.io.InputStream;

/**
 * An auxiliary class used for deserialization the YAML file to object.
 */
public final class YamlDeserializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory()).configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false
    );

    private YamlDeserializer() {
    }

    public static <T> T deserialize(InputStream inputStream, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, typeReference);
        } catch (Exception e) {
            throw new ExporterException("Failed to deserialize YAML data", e);
        }
    }
}
