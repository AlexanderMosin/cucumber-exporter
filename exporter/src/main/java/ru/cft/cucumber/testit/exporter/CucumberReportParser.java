package ru.cft.cucumber.testit.exporter;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.cft.cucumber.testit.exporter.json.JsonDeserializer;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberReport;

import java.nio.file.Paths;

/**
 * Represents class for parsing cucumber report.
 */
public final class CucumberReportParser {

    private CucumberReportParser() {
    }

    public static CucumberReport parse(String filePath) {
        return new CucumberReport(
                JsonDeserializer.deserialize(
                        Paths.get(filePath).toFile(),
                        new TypeReference<>() { }
                )
        );
    }
}
