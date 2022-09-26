package ru.cft.cucumber.testit.exporter;

import lombok.extern.slf4j.Slf4j;
import ru.cft.cucumber.testit.exporter.configuration.Configuration;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberElement;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberElementType;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberReport;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberScenario;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStep;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberStepRunStatus;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberTag;
import ru.cft.cucumber.testit.exporter.model.testit.AutotestChangeData;
import ru.cft.cucumber.testit.exporter.model.testit.TestRun;
import ru.cft.cucumber.testit.exporter.model.testit.Autotest;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunData;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunResult;
import ru.cft.cucumber.testit.exporter.model.testit.TestStepResult;

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

    private static final String AUTOTESTS_NAMESPACE = "Автотесты Qpay";

    private static final String ARCHIVE_NAMESPACE = "Архив";

    private static final String TAG_TEMPLATE = "@\\d{2,}";

    private static final String TAG_PREFIX = "@";

    private static final String TEST_NAME_SEPARATOR = "API.*";

    private final String projectId;

    private final String configurationId;

    private final String testRunMetadata;

    private final String jenkinsLink;

    public CucumberReportTransformer(Configuration configuration) {
        this.projectId = configuration.getProjectId();
        this.configurationId = configuration.getConfigurationId();
        this.testRunMetadata = configuration.getTestRunMetadata();
        this.jenkinsLink = configuration.getJenkinsLink();
    }

    public List<TestRunData> transformForPublishing(CucumberReport report) {
        Map<String, List<CucumberElement>> cucumberElements = new HashMap<>();
        for (CucumberScenario scenario : report.getScenarios()) {
            List<CucumberElement> elements = scenario.getElements()
                    .stream()
                    .filter(this::isValidCucumberScenario)
                    .collect(Collectors.toList());

            for (CucumberElement element : elements) {
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

    public AutotestChangeData transformForSynchronization(
            CucumberReport report,
            Map<String, Autotest> autotestsFromTestIt
    ) {
        Map<String, Autotest> autotestsFromReport = getAutotestsFromReport(report);
        List<Autotest> autotestsToArchive = new ArrayList<>();
        for (Map.Entry<String, Autotest> entry : autotestsFromTestIt.entrySet()) {
            String externalId = entry.getKey();
            Autotest autotest = entry.getValue();
            if (!autotestsFromReport.containsKey(externalId) && !autotest.getClassname().equals(ARCHIVE_NAMESPACE)) {
                autotest.setClassname(ARCHIVE_NAMESPACE);
                autotestsToArchive.add(autotest);
            }
        }

        List<Autotest> autotestsToCreate = new ArrayList<>();
        List<Autotest> autotestsToUpdate = new ArrayList<>();

        for (Map.Entry<String, Autotest> entry : autotestsFromReport.entrySet()) {
            String externalId = entry.getKey();
            if (autotestsFromTestIt.containsKey(externalId)) {
                Autotest autotestFromTestIt = autotestsFromTestIt.get(externalId);
                Autotest autotestFromReport = entry.getValue();
                if (!autotestFromTestIt.getName().equals(autotestFromReport.getName())
                        || !autotestFromTestIt.getTitle().equals(autotestFromReport.getTitle())
                        || !autotestFromTestIt.getClassname().equals(autotestFromReport.getClassname())
                        || !autotestFromTestIt.getNamespace().equals(autotestFromReport.getNamespace())
                ) {
                    autotestsToUpdate.add(autotestFromReport);
                }
            } else {
                autotestsToCreate.add(entry.getValue());
            }
        }

        return AutotestChangeData.builder()
                .autotestsToCreate(autotestsToCreate)
                .autotestsToUpdate(autotestsToUpdate)
                .autotestsToArchive(autotestsToArchive)
                .build();
    }

    private Map<String, Autotest> getAutotestsFromReport(CucumberReport report) {
        Map<String, Autotest> autotestsFromReport = new HashMap<>();
        for (CucumberScenario scenario : report.getScenarios()) {
            for (CucumberElement element : scenario.getElements()) {
                if (element.getType() == CucumberElementType.SCENARIO) {
                    String tagId = getTagId(element);
                    String name = getAutotestName(element, tagId);
                    Autotest autotest = Autotest.builder()
                            .externalId(tagId)
                            .projectId(projectId)
                            .name(name != null ? name : tagId)
                            .title(name)
                            .classname(scenario.getName())
                            .namespace(AUTOTESTS_NAMESPACE)
                            .build();
                    autotestsFromReport.put(tagId, autotest);
                }
            }
        }
        return autotestsFromReport;
    }

    private TestRun buildTestRun(String version, List<CucumberElement> cucumberElements) {
        List<String> autoTests = cucumberElements.stream()
                .filter(e -> e.getType() == CucumberElementType.SCENARIO)
                .map(this::getTagId)
                .collect(Collectors.toList());

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
                .autoTestExternalIds(autoTests)
                .build();
    }

    private List<TestRunResult> buildTestRunResults(List<CucumberElement> cucumberElements) {
        List<TestRunResult> testRunResults = new ArrayList<>(cucumberElements.size());

        for (CucumberElement cucumberElement : cucumberElements) {
            List<TestStepResult> stepResults = new ArrayList<>(cucumberElement.getSteps().size());
            for (CucumberStep step : cucumberElement.getSteps()) {
                if (step.getResult() != null && step.getResult().getStatus() != null) {
                    stepResults.add(new TestStepResult(step.getName(), step.getResult().getStatus().getValue()));
                } else {
                    throw new ExporterException(String.format("Failed to get status of the step %s", step.getName()));
                }
            }

            TestRunResult testRunResult = TestRunResult.builder()
                    .configurationId(configurationId)
                    .autoTestExternalId(getTagId(cucumberElement))
                    .outcome(convertToOutcome(cucumberElement))
                    .stepResults(stepResults)
                    .build();

            testRunResults.add(testRunResult);
        }

        return testRunResults;
    }

    private CucumberStepRunStatus calculateStepRunStatus(CucumberElement cucumberElement) {
        for (CucumberStep step : cucumberElement.getSteps()) {
            if (!step.getResult().getStatus().equals(PASSED)) {
                return FAILED;
            }
        }
        return PASSED;
    }

    // Statuses are used in cucumber:
    // PASSED, SKIPPED, PENDING, UNDEFINED, AMBIGUOUS, FAILED, UNUSED
    private String convertToOutcome(CucumberElement cucumberElement) {
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

    private String getAutotestName(CucumberElement cucumberElement, String tagId) {
        if (cucumberElement.getName() != null) {
            return cucumberElement.getName().replaceAll(TEST_NAME_SEPARATOR, "").trim();
        }
        log.warn("Failed to get name of test '{}'", tagId);
        return tagId;
    }

    private String getApiVersion(CucumberElement cucumberElement) {
        for (CucumberStep step : cucumberElement.getSteps()) {
            if (step.getName().contains(API_VERSION_PREFIX) && !step.getMatch().getArguments().isEmpty()) {
                return step.getMatch().getArguments().get(0).getValue();
            }
        }
        return "unknown version";
    }

    private String getTagId(CucumberElement cucumberElement) {
        for (CucumberTag tag : cucumberElement.getTags()) {
            if (tag.getName().matches(TAG_TEMPLATE)) {
                return tag.getName().replace(TAG_PREFIX, "");
            }
        }
        throw new ExporterException(String.format("Test %s doesn't have unique tag", cucumberElement.getName()));
    }

    private boolean isValidCucumberScenario(CucumberElement cucumberElement) {
        if (CucumberElementType.SCENARIO == cucumberElement.getType()) {
            for (CucumberTag tag : cucumberElement.getTags()) {
                if (tag.getName().matches(TAG_TEMPLATE)) {
                    return true;
                }
            }
        }
        return false;
    }
}
