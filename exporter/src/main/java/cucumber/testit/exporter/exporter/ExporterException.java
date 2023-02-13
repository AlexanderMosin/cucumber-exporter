package cucumber.testit.exporter.exporter;

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