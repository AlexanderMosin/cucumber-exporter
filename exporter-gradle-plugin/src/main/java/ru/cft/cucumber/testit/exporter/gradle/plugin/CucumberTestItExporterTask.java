package ru.cft.cucumber.testit.exporter.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import ru.cft.cucumber.testit.exporter.CucumberReportLoader;
import ru.cft.cucumber.testit.exporter.configuration.Configuration;

/**
 * Custom Gradle task for loading Cucumber report in TestIt.
 */
public class CucumberTestItExporterTask extends DefaultTask {

    /**
     * Path to cucumber report file
     */
    private String cucumberReportFilePath;

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
    private String testRunMetaData;

    /**
     * The link to Jenkins build.
     */
    private String jenkinsLink;

    @Option(option = "cucumber-report", description = "Path to cucumber report file")
    public void setCucumberReportFilePath(String cucumberReportFilePath) {
        this.cucumberReportFilePath = cucumberReportFilePath;
    }

    @Input
    public String getCucumberReportFilePath() {
        return cucumberReportFilePath;
    }

    @Option(option = "testit-url", description = "TestIt url")
    public void setUrl(String url) {
        this.url = url;
    }

    @Input
    public String getUrl() {
        return url;
    }

    @Option(option = "testit-token", description = "TestIt authentication API token")
    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    @Input
    public String getPrivateToken() {
        return privateToken;
    }

    @Option(option = "testit-project", description = "The identifier of TestIt project")
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Input
    public String getProjectId() {
        return projectId;
    }

    @Option(option = "testit-configuration", description = "The identifier of TestIt configuration")
    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    @Input
    public String getConfigurationId() {
        return configurationId;
    }

    @Option(option = "metadata", description = "The metadata of test run")
    public void setTestRunMetaData(String testRunMetaData) {
        this.testRunMetaData = testRunMetaData;
    }

    @Input
    public String getTestRunMetaData() {
        return testRunMetaData;
    }

    @Option(option = "jenkins-link", description = "The link to Jenkins build")
    public void setJenkinsLink(String jenkinsLink) {
        this.jenkinsLink = jenkinsLink;
    }

    @Input
    public String getJenkinsLink() {
        return jenkinsLink;
    }

    @TaskAction
    public void load() {
        Configuration configuration = new Configuration();
        configuration.setUrl(getUrl());
        configuration.setPrivateToken(getPrivateToken());
        configuration.setProjectId(getProjectId());
        configuration.setConfigurationId(getConfigurationId());
        configuration.setTestRunMetadata(getTestRunMetaData());
        configuration.setJenkinsLink(getJenkinsLink());

        CucumberReportLoader reportLoader = new CucumberReportLoader(configuration);
        reportLoader.load(cucumberReportFilePath);
    }
}
