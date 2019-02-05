package io.github.jaqat.remoterobot.selenoid;

import com.jayway.jsonpath.JsonPath;
import io.github.jaqat.remoterobot.client.RemoteRobot;
import org.apache.mina.util.Base64;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static io.github.jaqat.remoterobot.selenoid.SelenoidUtils.getRemoteRobotExposedPort;
import static java.util.stream.Collectors.joining;

public class GgrUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GgrUtils.class);

    public static RemoteRobot getRemoteRobot(URL ggrUrl, RemoteWebDriver remoteWebDriver) {
        URL remoteRobotUrl;
        try {

            URL selenoidUrl = getSelenoidUrlOfCurrentSession(ggrUrl, remoteWebDriver.getSessionId());
            SessionId selenoidSessionId = new SessionId(remoteWebDriver.getSessionId().toString().substring(32));

            remoteRobotUrl = new URL(
                    String.format(
                            "%s://%s:%s",
                            selenoidUrl.getProtocol(),
                            selenoidUrl.getHost(),
                            getRemoteRobotExposedPort(selenoidUrl, selenoidSessionId)
                    )
            );
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid url of Remote Robot Server: " + e.getMessage());
        }
        return new RemoteRobot(remoteRobotUrl);
    }

    static URL getSelenoidUrlOfCurrentSession(URL ggrUrl, SessionId sessionId) {
        HttpURLConnection connection = null;
        try {
            LOGGER.info("##### Get selenoid's status");
            URL ggrSessionHostInfoUrl = new URL(ggrUrl, "/host/" + sessionId);
            connection = ((HttpURLConnection) ggrSessionHostInfoUrl.openConnection());
            connection.setRequestMethod("GET");
            try {
                String authStr = ggrUrl.getAuthority().substring(
                        0,
                        ggrUrl.getAuthority().indexOf("@")
                );
                connection.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(authStr.getBytes())));
            } catch (StringIndexOutOfBoundsException outBoundsException){
                throw new IllegalStateException("GGR url must contains authorization data", outBoundsException);
            }
            LOGGER.info("### Request: (" + connection.getRequestMethod() + ") " + connection.getURL());
            int status = connection.getResponseCode();
            if (status == -1 || status > 299) {
                throw new IllegalStateException("Can't get GGR's session host info. Response code: " + status);
            }
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String responseBody = buffer.lines().collect(joining("\n"));
                LOGGER.info("### Response:\n" + responseBody);
                try {
                    String selenoidHost = JsonPath.read(responseBody, "$.Name");
                    return new URL(
                            String.format(
                                    "http://%s:%d",
                                    "docker.for.mac.localhost".equals(selenoidHost) ? "localhost" : selenoidHost,
                                    JsonPath.read(responseBody, "$.Port")
                            )
                    );
                } catch (MalformedURLException e){
                    throw new IllegalStateException("Wrong generated Selenoid url", e);
                }
            } catch (IOException ioe) {
                throw new RuntimeException("Error while reading Selenoid's status", ioe);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Can't get Selenoid's status", ioe);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        //HttpURLConnection connection =


        URL ggrSessionHostInfoUrl = new URL("http://test:test@localhost:4000/host/c2b93f672bde998188f0bea0f2cb219087f8bb791e5288a46c07cd216ed77edf");
        //URL ggrSessionHostInfoUrl = new URL("http://test:test@localhost:4000/host");
        //URL ggrSessionHostInfoUrl = new URL("http://localhost:4001/status");
        HttpURLConnection connection = ((HttpURLConnection) ggrSessionHostInfoUrl.openConnection());
        connection.setRequestProperty("Authorization", "Basic dGVzdDp0ZXN0");
        connection.setRequestMethod("GET");
        System.out.println("status: " + connection.getResponseCode());

    }
}
