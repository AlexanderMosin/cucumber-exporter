package ru.cft.cucumber.testit.exporter;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

@Slf4j
public final class HttpUtils {

    private static final String PRIVATE_TOKEN_PREFIX = "PrivateToken ";

    private HttpUtils() {
    }

    public static HttpResponse sendRequest(Request request, String privateToken, int expectedStatusCode) {
        try {
            HttpResponse response = request.addHeader(HttpHeaders.AUTHORIZATION, PRIVATE_TOKEN_PREFIX + privateToken)
                    .execute()
                    .returnResponse();
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("Request: {}. Response status: {}", request, statusCode);

            if (statusCode != expectedStatusCode) {
                log.error("Response body:\n{}", EntityUtils.toString(response.getEntity()));
                throw new ExporterException(String.format("Wrong status. Expected status: %d", expectedStatusCode));
            }

            return response;
        } catch (Exception e) {
            throw new ExporterException(String.format("Error occurred while sending %s: ", request.toString()), e);
        }
    }
}
