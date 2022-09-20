package ru.cft.cucumber.testit.exporter;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import ru.cft.cucumber.testit.exporter.configuration.Configuration;
import ru.cft.cucumber.testit.exporter.json.JsonSerializer;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberReport;
import ru.cft.cucumber.testit.exporter.model.testit.Autotest;
import ru.cft.cucumber.testit.exporter.model.testit.AutotestChangeData;
import ru.cft.cucumber.testit.exporter.model.testit.TestRun;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunData;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunResult;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static ru.cft.cucumber.testit.exporter.json.JsonDeserializer.deserialize;


/**
 * Represents class for loading Cucumber report in TestItConfiguration.
 */
@Slf4j
@SuppressWarnings("squid:S1075")
public class CucumberReportLoader {

    private static final String TEST_RUNS_PATH = "/v2/testRuns/byAutotests";

    private static final String TEST_RESULTS_PATH = "/v2/testRuns/%s/testResults";

    private static final String AUTOTESTS_PATH = "/v2/autoTests";

    private static final String ID_FIELD = "id";

    private static final String IS_DELETED_PARAM = "isDeleted=";

    private static final String PROJECT_ID_PARAM = "projectId=";

    private static final String SKIP_PARAM = "skip=";

    private static final String TAKE_PARAM = "take=";

    private static final int BATCH_SIZE = 100;

    private final Configuration configuration;

    public CucumberReportLoader(Configuration configuration) {
        this.configuration = configuration;
    }

    public void load(String cucumberReportFilePath) {
        CucumberReport report = CucumberReportParser.parse(cucumberReportFilePath);
        CucumberReportTransformer reportTransformer = new CucumberReportTransformer(configuration);

        synchronizeAutotestStructure(report, reportTransformer);
        publishResults(report, reportTransformer);
    }

    private void publishResults(CucumberReport report, CucumberReportTransformer reportTransformer) {
        List<TestRunData> testRunsData = reportTransformer.transformForPublishing(report);
        for (TestRunData testRunData : testRunsData) {
            String testRunId = createTestRun(testRunData.getTestRun());
            addResultsToTestRun(testRunId, testRunData.getTestRunResults());
        }
    }

    private void synchronizeAutotestStructure(CucumberReport report, CucumberReportTransformer reportTransformer) {
        Map<String, Autotest> autotestsFromTestIt = getAutotests();
        AutotestChangeData autotestData = reportTransformer.transformForSynchronization(report, autotestsFromTestIt);
        autotestData.getAutotestsToCreate().forEach(this::createAutotest);
        autotestData.getAutotestsToUpdate().forEach(this::updateAutotest);
        autotestData.getAutotestsToArchive().forEach(this::updateAutotest);
    }

    private String createTestRun(TestRun testRun) {
        HttpResponse response = sendPostRequest(TEST_RUNS_PATH, testRun, HttpStatus.SC_CREATED);

        String testRunId;
        try {
            Map<String, Object> map = deserialize(EntityUtils.toString(response.getEntity()), new TypeReference<>() {});
            testRunId = map.get(ID_FIELD).toString();
        } catch (Exception e) {
            throw new ExporterException("Failed to get testRunId from response ", e);
        }

        return testRunId;
    }

    private void addResultsToTestRun(String testRunId,  List<TestRunResult> testRunResults) {
        sendPostRequest(
                String.format(TEST_RESULTS_PATH, testRunId),
                testRunResults,
                HttpStatus.SC_OK
        );
    }

    private void createAutotest(Autotest autotest) {
        sendPostRequest(AUTOTESTS_PATH, autotest, HttpStatus.SC_CREATED);
    }

    private void updateAutotest(Autotest autotest) {
        sendPutRequest(AUTOTESTS_PATH, autotest);
    }

    private Map<String, Autotest> getAutotests() {
        Set<Autotest> autotests = new HashSet<>();
        int offset = 0;
        HttpResponse response;

        do {
            String params = PROJECT_ID_PARAM + configuration.getProjectId() + "&" + SKIP_PARAM + offset + "&"
                    + TAKE_PARAM + BATCH_SIZE + "&" + IS_DELETED_PARAM + "false";
            response = sendGetRequest(AUTOTESTS_PATH + "/" + "?" + params);
            try {
                autotests.addAll(deserialize(EntityUtils.toString(response.getEntity()), new TypeReference<>() {}));
            } catch (IOException e) {
                throw new ExporterException("Failed to get autotests from response", e);
            }
            offset += BATCH_SIZE;
        }
        while (response.getEntity().getContentLength() > 2);

        return autotests.stream().collect(Collectors.toMap(Autotest::getExternalId, Function.identity()));
    }

    private HttpResponse sendPostRequest(String path, Object requestBody, int expectedStatusCode) {
        String json = JsonSerializer.serialize(requestBody);
        Request request = Request.Post(configuration.getUrl() + path).bodyString(json, APPLICATION_JSON);

        return HttpUtils.sendRequest(request, configuration.getPrivateToken(), expectedStatusCode);
    }

    private HttpResponse sendGetRequest(String path) {
        Request request = Request.Get(configuration.getUrl() + path);
        return HttpUtils.sendRequest(request, configuration.getPrivateToken(), HttpStatus.SC_OK);
    }

    private void sendPutRequest(String path, Object requestBody) {
        String json = JsonSerializer.serialize(requestBody);
        Request request = Request.Put(configuration.getUrl() + path).bodyString(json, APPLICATION_JSON);
        HttpUtils.sendRequest(request, configuration.getPrivateToken(), HttpStatus.SC_NO_CONTENT);
    }
}
