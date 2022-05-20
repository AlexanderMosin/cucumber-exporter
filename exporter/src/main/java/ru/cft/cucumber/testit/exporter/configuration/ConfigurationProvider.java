package ru.cft.cucumber.testit.exporter.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import ru.cft.cucumber.testit.exporter.ExporterException;
import ru.cft.cucumber.testit.exporter.yaml.YamlDeserializer;

import java.io.InputStream;

/**
 * The class for reading configuration from yaml-files.
 */
public final class ConfigurationProvider {

    private static final String CONFIGURATION_FILENAME = "application.yaml";

    private ConfigurationProvider() {
    }

    public static Configuration get() {
        InputStream stream = ConfigurationProvider.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILENAME);
        if (stream == null) {
            throw new ExporterException(String.format("Unable to read file %s", CONFIGURATION_FILENAME));
        }
        return YamlDeserializer.deserialize(stream, new TypeReference<Data>() { }).getConfiguration();
    }

    @Getter
    @Setter
    private static class Data {

        @JsonProperty("testIt")
        private Configuration configuration;

    }
}