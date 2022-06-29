package ru.cft.cucumber.testit.exporter;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import ru.cft.cucumber.testit.exporter.configuration.Configuration;
import ru.cft.cucumber.testit.exporter.json.JsonSerializer;
import ru.cft.cucumber.testit.exporter.model.cucumber.CucumberReport;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunData;
import ru.cft.cucumber.testit.exporter.model.testit.TestRunResult;
import ru.cft.cucumber.testit.exporter.model.testit.TestRun;

import java.util.List;
import java.util.Map;

import static ru.cft.cucumber.testit.exporter.json.JsonDeserializer.deserialize;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;


/**
 * Represents class for loading Cucumber report in TestItConfiguration.
 */
@Slf4j
@SuppressWarnings("squid:S1075")
public class CucumberReportLoader {

    private static final String TEST_RUNS_PATH = "/v2/testRuns/byAutotests";

    private static final String TEST_RESULTS_PATH = "/v2/testRuns/%s/testResults";

    private static final String PRIVATE_TOKEN_PREFIX = "PrivateToken ";

    private static final String ID_FIELD = "id";

    private final Configuration configuration;

    public CucumberReportLoader(Configuration configuration) {
        this.configuration = configuration;
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

    public void load(String cucumberReportFilePath) {
        CucumberReport report = CucumberReportParser.parse(cucumberReportFilePath);

        CucumberReportTransformer reportTransformer = new CucumberReportTransformer(
                configuration.getProjectId(),
                configuration.getConfigurationId(),
                configuration.getTestRunMetadata(),
                configuration.getJenkinsLink()
        );

        List<TestRunData> testRunsData = reportTransformer.transform(report);
        for (TestRunData testRunData : testRunsData) {
            String testRunId = createTestRun(testRunData.getTestRun());
            addResultsToTestRun(testRunId, testRunData.getTestRunResults());
        }
    }

    private HttpResponse sendPostRequest(String path, Object requestBody, int expectedStatusCode) {
        String json = JsonSerializer.serialize(requestBody);
        try {
            HttpResponse response = Request.Post(configuration.getUrl() + path)
                    .bodyString(json, APPLICATION_JSON)
                    .addHeader(HttpHeaders.AUTHORIZATION, PRIVATE_TOKEN_PREFIX + configuration.getPrivateToken())
                    .execute()
                    .returnResponse();

            int statusCode = response.getStatusLine().getStatusCode();
            log.info("POST request: {}", configuration.getUrl() + path);
            log.info("Response status: {}", statusCode);

            if (statusCode != expectedStatusCode) {
                log.error("Request body:\n{}", json);
                log.error("Response body:\n{}", EntityUtils.toString(response.getEntity()));
                throw new ExporterException(String.format("Wrong status. Expected status: %d", expectedStatusCode));
            }
            return response;
        } catch (Exception e) {
            throw new ExporterException(String.format("Error occurred while sending POST %s:%n", path), e);
        }
    }
}
