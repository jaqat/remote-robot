package com.github.jaqat.remoterobot.selenoid;

import com.jayway.jsonpath.JsonPath;
import com.github.jaqat.remoterobot.client.RemoteRobot;
import net.minidev.json.JSONArray;
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
import java.util.Map;

import static com.github.jaqat.remoterobot.commons.config.Config.REMOTE_ROBOT_SERVER_PORT;
import static java.util.stream.Collectors.joining;

public class SelenoidUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelenoidUtils.class);

    public static RemoteRobot getRemoteRobot(URL selenoidUrl, RemoteWebDriver remoteWebDriver) {
        URL remoteRobotUrl;
        try {
            remoteRobotUrl = new URL(
                    String.format(
                            "%s://%s:%s",
                            selenoidUrl.getProtocol(),
                            selenoidUrl.getHost(),
                            getRemoteRobotExposedPort(selenoidUrl, remoteWebDriver.getSessionId())
                    )
            );
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid url of Remote Robot Server: " + e.getMessage());
        }
        return new RemoteRobot(remoteRobotUrl);
    }

    static String getRemoteRobotExposedPort(URL selenoidUrl, SessionId sessionId) {
        HttpURLConnection connection = null;
        try {
            LOGGER.info("##### Get selenoid's status");
            URL selenoidStatusUrl = new URL(selenoidUrl, "/status");
            connection = ((HttpURLConnection) selenoidStatusUrl.openConnection());
            connection.setRequestMethod("GET");
            LOGGER.info("### Request : " + connection.getRequestMethod() + " " + connection.getURL());
            connection = (HttpURLConnection) selenoidStatusUrl.openConnection();
            int status = connection.getResponseCode();
            if (status == -1 || status > 299) {
                throw new IllegalStateException("Can't get Selenoid's status. Response code: " + status);
            }
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String responseBody = buffer.lines().collect(joining("\n"));
                LOGGER.info("### Response:\n" + responseBody);
                JSONArray exposedPortsRaw = JsonPath.read(responseBody, "$..sessions[?(@.id=='" + sessionId.toString() + "')].containerInfo.exposedPorts");
                if (exposedPortsRaw.size() != 1) {
                    throw new IllegalStateException("Can't detect exposed ports of Selenoid's container for session: " + sessionId);
                }
                Map<String, String> exposedPorts = (Map<String, String>) exposedPortsRaw.get(0);
                if (exposedPorts.get(REMOTE_ROBOT_SERVER_PORT) == null) {
                    throw new IllegalStateException("RemoteRobotServer's port" + REMOTE_ROBOT_SERVER_PORT + " is not published");
                }
                return exposedPorts.get(REMOTE_ROBOT_SERVER_PORT);
            } catch (IOException ioe) {
                throw new RuntimeException("Error while reading Selenoid's status");
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Can't get Selenoid's status: " + ioe.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
