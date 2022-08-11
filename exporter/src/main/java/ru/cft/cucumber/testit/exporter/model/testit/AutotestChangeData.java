package ru.cft.cucumber.testit.exporter.model.testit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Contains information about the autotests to be created, deleted and updated
 */
@Getter
@Setter
@Builder(builderClassName = "Builder", toBuilder = true)
public class AutotestChangeData {

    /**
     * Autotests that need to be created
     */
    private List<Autotest> autotestsToCreate;

    /**
     * Autotests that need to be updated
     */
    private List<Autotest> autotestsToUpdate;

    /**
     * Autotests that need to be deleted
     */
    private List<Autotest> autotestsToArchive;
}
