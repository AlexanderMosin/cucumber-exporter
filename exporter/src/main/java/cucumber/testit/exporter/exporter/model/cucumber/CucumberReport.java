package cucumber.testit.exporter.exporter.model.cucumber;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cucumber report.
 */
@Getter
@Setter
public class CucumberReport {

    /**
     * Scenarios including in the report.
     */
    private List<CucumberScenario> scenarios;

    public CucumberReport(List<CucumberScenario> scenarios) {
        this.scenarios = scenarios;
    }

    public CucumberReport() {
        this.scenarios = new ArrayList<>();
    }

    public void addScenarios(List<CucumberScenario> scenarios) {
        this.scenarios.addAll(scenarios);
    }
}
