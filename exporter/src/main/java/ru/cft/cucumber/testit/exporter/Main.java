package ru.cft.cucumber.testit.exporter;

import lombok.extern.slf4j.Slf4j;
import ru.cft.cucumber.testit.exporter.configuration.Configuration;
import ru.cft.cucumber.testit.exporter.configuration.ConfigurationProvider;

/**
 * Represents entry point to export cucumber report in TestIt.
 */
@Slf4j
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Should set the argument with full path to the cucumber report file in json format");
        } else {
            Configuration configuration = ConfigurationProvider.get();
            CucumberReportLoader reportLoader = new CucumberReportLoader(configuration);
            reportLoader.load(args[0]);
        }
    }
}
