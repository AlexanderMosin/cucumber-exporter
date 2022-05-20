package ru.cft.cucumber.testit.exporter;

import lombok.extern.slf4j.Slf4j;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberReport;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberTestStatus;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunResult;
import ru.cft.cucumber.testit.exporter.model.testit.TestStepResult;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberScenario;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStep;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberElement;
import ru.cft.cucumber.testit.exporter.model.testit.TestRun;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberTestStatus.FAILED;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberTestStatus.PASSED;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberTestStatus.SKIPPED;
import static ru.cft.cucumber.testit.exporter.model.cucumber.CucumberTestStatus.UNDEFINED;

/**
 * An auxiliary class used for building TestIt request objects.
 */
@Slf4j
public class TestItObjectBuilder {
    
    private static final String TEST_NAME_SEPARATOR = "API";
    
    private final CucumberReport report;

    private final String projectId;

    private final String configurationId;

    public TestItObjectBuilder(CucumberReport report, String projectId, String configurationId) {
        this.report = report;
        this.projectId = projectId;
        this.configurationId = configurationId;
    }

    public TestRun buildTestRun() {
        List<String> autoTestsExternalIds = new ArrayList<>();

        for (CucumberScenario scenario : report.getScenarios()) {
            for (CucumberElement element : scenario.getElements()) {
                autoTestsExternalIds.add(getExternalId(element));
            }
        }

        return TestRun.builder()
                .projectId(projectId)
                .configurationIds(List.of(configurationId))
                .name("Test run <releaseVersion> " + LocalDateTime.now().format(ofLocalizedDateTime(FormatStyle.MEDIUM)))
                .description("<branch_name> http://jenkinslink.com")
                .launchSource("From Jenkins")
                .autoTestExternalIds(autoTestsExternalIds)
                .build();
    }

    public List<TestRunResult> buildTestRunResults() {
        List<TestRunResult> testRunResults = new ArrayList<>();

        for (CucumberScenario scenario : report.getScenarios()) {
            for (CucumberElement element : scenario.getElements()) {
                List<TestStepResult> stepResults = new ArrayList<>();
                for (CucumberStep step : element.getSteps()) {
                    stepResults.add(
                            TestStepResult.builder()
                                    .title(step.getName())
                                    .outcome(step.getResult().getStatus().getValue())
                                    .build()
                    );
                }

                testRunResults.add(
                        TestRunResult.builder()
                                .configurationId(configurationId)
                                .autoTestExternalId(getExternalId(element))
                                .outcome(convertToOutcome(element))
                                .stepResults(stepResults)
                                .build()
                );
            }
        }

        return testRunResults;
    }

    private static String getExternalId(CucumberElement cucumberElement) {
        return cucumberElement.getName().split(TEST_NAME_SEPARATOR)[0].trim();
    }

    private static CucumberTestStatus calculateTestStatus(CucumberElement cucumberElement) {
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
        CucumberTestStatus status = calculateTestStatus(cucumberElement);
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
}
