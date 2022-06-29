package ru.cft.cucumber.testit.exporter;

import lombok.extern.slf4j.Slf4j;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberReport;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberScenario;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStepRunStatus;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunData;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunResult;
import ru.cft.cucumber.testit.exporter.model.testit.TestStepResult;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStep;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberElement;
import ru.cft.cucumber.testit.exporter.model.testit.TestRun;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStepRunStatus.FAILED;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStepRunStatus.PASSED;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStepRunStatus.SKIPPED;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStepRunStatus.UNDEFINED;

/**
 * An auxiliary class used for transforming cucumber report in TestIt request objects.
 */
@Slf4j
public class CucumberReportTransformer {

    private static final String API_VERSION_PREFIX = "Версия API: ";

    private static final String TEST_NAME_SEPARATOR = "API";

    private final String projectId;

    private final String configurationId;

    private final String testRunMetadata;

    private final String jenkinsLink;

    public CucumberReportTransformer(
            String projectId,
            String configurationId,
            String testRunMetadata,
            String jenkinsLink
    ) {
        this.projectId = projectId;
        this.configurationId = configurationId;
        this.testRunMetadata = testRunMetadata;
        this.jenkinsLink = jenkinsLink;
    }

    public List<TestRunData> transform(CucumberReport report) {
        Map<String, List<CucumberElement>> cucumberElements = new HashMap<>();
        for (CucumberScenario scenario : report.getScenarios()) {
            for (CucumberElement element : scenario.getElements()) {
                String version = getApiVersion(element);
                cucumberElements.putIfAbsent(version, new ArrayList<>());
                cucumberElements.get(version).add(element);
            }
        }

        List<TestRunData> testRunsData = new ArrayList<>(cucumberElements.size());
        for (Map.Entry<String, List<CucumberElement>> entry : cucumberElements.entrySet()) {
            TestRunData testRunData = new TestRunData();
            testRunData.setTestRun(buildTestRun(entry.getKey(), entry.getValue()));
            testRunData.setTestRunResults(buildTestRunResults(entry.getValue()));
            testRunsData.add(testRunData);
        }

        return testRunsData.stream()
                .sorted((data1, data2) -> data2.getTestRun().getName().compareToIgnoreCase(data1.getTestRun().getName()))
                .collect(Collectors.toList());
    }

    private TestRun buildTestRun(String version, List<CucumberElement> cucumberElements) {
        List<String> autoTestsExternalIds = new ArrayList<>(cucumberElements.size());
        cucumberElements.forEach(element -> autoTestsExternalIds.add(getExternalId(element)));

        return TestRun.builder()
                .projectId(projectId)
                .configurationIds(List.of(configurationId))
                .name(
                        String.format(
                                "Test run for API version %s, %s, %s",
                                version,
                                testRunMetadata,
                                LocalDateTime.now().format(ofLocalizedDateTime(FormatStyle.SHORT))
                        )
                )
                .description(String.format("Jenkins task: %s", jenkinsLink))
                .launchSource("From Jenkins")
                .autoTestExternalIds(autoTestsExternalIds)
                .build();
    }

    private List<TestRunResult> buildTestRunResults(List<CucumberElement> cucumberElements) {
        List<TestRunResult> testRunResults = new ArrayList<>(cucumberElements.size());

        for (CucumberElement cucumberElement : cucumberElements) {
            List<TestStepResult> stepResults = new ArrayList<>(cucumberElement.getSteps().size());
            for (CucumberStep step : cucumberElement.getSteps()) {
                stepResults.add(
                        TestStepResult.builder()
                                .title(step.getName())
                                .outcome(step.getResult().getStatus().getValue())
                                .build()
                );
            }

            TestRunResult testRunResult = TestRunResult.builder()
                    .configurationId(configurationId)
                    .autoTestExternalId(getExternalId(cucumberElement))
                    .outcome(convertToOutcome(cucumberElement))
                    .stepResults(stepResults)
                    .build();

            testRunResults.add(testRunResult);
        }

        return testRunResults;
    }

    private static String getExternalId(CucumberElement cucumberElement) {
        if (cucumberElement.getName() != null) {
            String[] names = cucumberElement.getName().split(TEST_NAME_SEPARATOR);
            if (names.length > 1) {
                return names[0].trim();
            }
        }
        throw new ExporterException("Failed to get test name");
    }

    private static CucumberStepRunStatus calculateStepRunStatus(CucumberElement cucumberElement) {
        for (CucumberStep step : cucumberElement.getSteps()) {
            if (!step.getResult().getStatus().equals(PASSED)) {
                return FAILED;
            }
        }
        return PASSED;
    }

    // Statuses are used in cucumber:
    // PASSED, SKIPPED, PENDING, UNDEFINED, AMBIGUOUS, FAILED, UNUSED
    private static String convertToOutcome(CucumberElement cucumberElement) {
        CucumberStepRunStatus status = calculateStepRunStatus(cucumberElement);
        String outcome;

        if (status.equals(PASSED)) {
            outcome = PASSED.getValue();
        } else if (status.equals(FAILED)) {
            outcome = FAILED.getValue();
        } else if (status.equals(SKIPPED)) {
            outcome = SKIPPED.getValue();
        } else {
            log.warn("Unrecognized step or test result {} was found", status.getValue());
            outcome = UNDEFINED.getValue();
        }

        return outcome;
    }

    private static String getApiVersion(CucumberElement element) {
        for (CucumberStep step : element.getSteps()) {
            if (step.getName().contains(API_VERSION_PREFIX) && !step.getMatch().getArguments().isEmpty()) {
                return step.getMatch().getArguments().get(0).getValue();
            }
        }
        return "unknown version";
    }
}
