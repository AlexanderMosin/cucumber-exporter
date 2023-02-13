package cucumber.testit.exporter.exporter;

import com.fasterxml.jackson.core.type.TypeReference;
import cucumber.testit.exporter.exporter.json.JsonDeserializer;
import cucumber.testit.exporter.exporter.model.cucumber.CucumberReport;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents class for parsing cucumber report.
 */
@Slf4j
public final class CucumberReportParser {

    private static final int DEPTH = 2;

    private CucumberReportParser() {
    }

    public static CucumberReport parse(String pathToReport) {
        Path path = Paths.get(pathToReport);
        return Files.isDirectory(path) ? parseFilesInDirectory(path) : parseFile(path);
    }

    private static CucumberReport parseFile(Path pathToFile) {
        if (!Files.exists(pathToFile)) {
            throw new ExporterException(String.format("File %s doesn't exist", pathToFile));
        }
        if (pathToFile.toFile().length() == 0) {
            throw new ExporterException(String.format("File %s is empty", pathToFile));
        }

        return new CucumberReport(JsonDeserializer.deserialize(pathToFile.toFile(), new TypeReference<>() { }));
    }

    private static CucumberReport parseFilesInDirectory(Path pathToDirectory) {
        CucumberReport cucumberReport = new CucumberReport();

        try (Stream<Path> stream = Files.walk(pathToDirectory, DEPTH)) {
            List<Path> paths = stream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toList());
            if (paths.isEmpty()) {
                throw new ExporterException(String.format("Directory %s is empty", pathToDirectory));
            }
            paths.forEach(p -> cucumberReport.addScenarios(parseFile(p).getScenarios()));
        } catch (Exception e) {
            throw new ExporterException(String.format("Unable to read files from directory '%s'", pathToDirectory), e);
        }

        return cucumberReport;
    }
}
