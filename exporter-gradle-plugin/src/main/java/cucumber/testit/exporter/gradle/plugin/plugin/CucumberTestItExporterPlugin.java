package cucumber.testit.exporter.gradle.plugin.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;

/**
 * The Gradle plugin that creates exportCucumberReport task.
 */
public class CucumberTestItExporterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("exportCucumberReport", CucumberTestItExporterTask.class);
        project.getLogging().captureStandardOutput(LogLevel.LIFECYCLE);
    }

}
