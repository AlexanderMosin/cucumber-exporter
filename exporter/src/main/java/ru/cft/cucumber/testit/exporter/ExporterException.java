package ru.cft.cucumber.testit.exporter;

/**
 * The class for exceptions thrown by exporter.
 */
public class ExporterException extends RuntimeException {

    public ExporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExporterException(String message) {
        super(message);
    }
}