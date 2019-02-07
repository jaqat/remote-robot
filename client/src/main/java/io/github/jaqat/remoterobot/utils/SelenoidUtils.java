package io.github.jaqat.remoterobot.utils;

import com.jayway.jsonpath.JsonPath;
import io.github.jaqat.remoterobot.client.RemoteRobot;
import javafx.util.Pair;
import net.minidev.json.JSONArray;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static io.github.jaqat.remoterobot.config.Config.REMOTE_ROBOT_SERVER_PORT;
import static java.util.stream.Collectors.joining;

public class SelenoidUtils {

    /**
     * Get RemoteRobot instance for remote browser created by Selenoid
     *
     * @param remoteWebDriver instance of RemoteWebDriver
     * @return {@link RemoteRobot} instance
     */
    public static RemoteRobot getRemoteRobot(RemoteWebDriver remoteWebDriver) {
        try {
            URL remoteServerAddress = ((HttpCommandExecutor) remoteWebDriver.getCommandExecutor()).getAddressOfRemoteServer();
            return new RemoteRobot(
                    new URL(
                            remoteServerAddress.getProtocol(),
                            remoteServerAddress.getHost(),
                            getRemoteRobotPublishedPort(
                                    getSessionInfo(
                                            getResource(new URL(remoteServerAddress.getProtocol(), remoteServerAddress.getHost(), remoteServerAddress.getPort(), "/status")),
                                            remoteWebDriver.getSessionId()
                                    )
                            ),
                            ""
                    )
            );
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid url of Remote Robot Server: " + e.getMessage());
        }
    }

    /**
     * Get response body of requested URL
     *
     * @param url
     * @param requestProperties HttpURLConnection's properties (not required)
     * @return response body's string
     */
    static String getResource(URL url, Pair<String, String>... requestProperties) {
        HttpURLConnection connection = null;
        try {
            connection = ((HttpURLConnection) url.openConnection());
            connection.setRequestMethod("GET");
            for (Pair<String, String> property : requestProperties){
                connection.setRequestProperty(property.getKey(), property.getValue());
            }
            int status = connection.getResponseCode();
            if (status == -1 || status > 299) {
                throw new IllegalStateException(String.format("Can't get resource: %s. Response code: %d", url, status));
            }
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String responseBody = buffer.lines().collect(joining("\n"));
                return responseBody;
            } catch (IOException ioe) {
                throw new RuntimeException("Error while reading http response", ioe);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while performing http request: " + url);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Parse json object with session info from Selenoid's status response
     *
     * @param selenoidStatus response body of Selenoid's /status
     * @param sessionId remoteWebDriver's session id
     * @return json with session info
     */
    static Object getSessionInfo(String selenoidStatus, SessionId sessionId) {
        JSONArray foundedSessions = JsonPath.read(selenoidStatus, "$..sessions[?(@.id=='" + sessionId.toString() + "')]");
        if (foundedSessions.size() != 1) {
            throw new IllegalStateException(
                    String.format(
                            "Selenoid doesn't contains session: %s.\nStatus:\n%s" + sessionId, selenoidStatus));
        }
        return foundedSessions.get(0);
    }

    /**
     * Parse published RemoteRobot server's port from session info json
     *
     * @param selenoidSessionInfo session info json
     * @return published port of internal 5555 port of Selenoid docker container with browser
     */
    static int getRemoteRobotPublishedPort(Object selenoidSessionInfo) {
        Map<String, String> exposedPorts = JsonPath.read(selenoidSessionInfo, "$.containerInfo.exposedPorts");
        if (exposedPorts.get(REMOTE_ROBOT_SERVER_PORT) == null) {
            throw new IllegalStateException("RemoteRobotServer's port" + REMOTE_ROBOT_SERVER_PORT + " is not published");
        }
        return Integer.parseInt(exposedPorts.get(REMOTE_ROBOT_SERVER_PORT));
    }
}
