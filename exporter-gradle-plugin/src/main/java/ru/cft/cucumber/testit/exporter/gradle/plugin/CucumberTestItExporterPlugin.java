package ru.cft.cucumber.testit.exporter.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The Gradle plugin that creates exportCucumberReport task.
 */
public class CucumberTestItExporterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("exportCucumberReport", CucumberTestItExporterTask.class);
    }

}
