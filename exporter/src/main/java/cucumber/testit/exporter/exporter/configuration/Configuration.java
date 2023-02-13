package cucumber.testit.exporter.exporter.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents configuration class for loading Cucumber report in TestIt.
 */
@Getter
@Setter
public class Configuration {

    /**
     * TestIt url
     */
    private String url;

    /**
     * TestIt authentication API token
     */
    private String privateToken;

    /**
     * The identifier of TestIt project.
     */
    private String projectId;

    /**
     * The identifier of TestIt configuration.
     */
    private String configurationId;

    /**
     * The metadata of test run.
     */
    private String testRunMetadata;

    /**
     * The link to Jenkins build.
     */
    private String jenkinsLink;
}
