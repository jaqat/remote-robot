package io.github.jaqat.remoterobot.utils;

import com.jayway.jsonpath.JsonPath;
import io.github.jaqat.remoterobot.client.RemoteRobot;
import javafx.util.Pair;
import org.apache.mina.util.Base64;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.net.MalformedURLException;
import java.net.URL;

import static io.github.jaqat.remoterobot.utils.SelenoidUtils.getRemoteRobotPublishedPort;
import static io.github.jaqat.remoterobot.utils.SelenoidUtils.getResource;
import static io.github.jaqat.remoterobot.utils.SelenoidUtils.getSessionInfo;

public class GgrUtils {

    public static RemoteRobot getRemoteRobot(RemoteWebDriver remoteWebDriver) {
        try {
            URL remoteServer = ((HttpCommandExecutor) remoteWebDriver.getCommandExecutor()).getAddressOfRemoteServer();

            URL selenoidUrl = getSelenoidUrl(
                    new URL(
                            remoteServer.getProtocol(),
                            remoteServer.getHost(),
                            remoteServer.getPort(),
                            "/host/" + remoteWebDriver.getSessionId()
                    ),
                    remoteServer.getUserInfo()
            );

            return new RemoteRobot(
                    new URL(
                            selenoidUrl.getProtocol(),
                            selenoidUrl.getHost(),
                            getRemoteRobotPublishedPort(
                                    getSessionInfo(
                                            getResource(new URL(selenoidUrl.getProtocol(), selenoidUrl.getHost(), selenoidUrl.getPort(), "/status")),
                                            getSelenoidSessionId(remoteWebDriver.getSessionId())
                                    )
                            ),
                            ""
                    )
            );
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get Selenoid's SessionId from GGR's SessionId
     *
     * @param ggrSessionId
     * @return
     */
    static private SessionId getSelenoidSessionId(SessionId ggrSessionId) {
        return new SessionId(ggrSessionId.toString().substring(32));
    }

    static private URL getSelenoidUrl(URL ggrSessionHostInfoUrl, String auth) {
        if (auth == null) {
            throw new IllegalStateException("There is no user info for authorization");
        }
        try {
            String sessionHostInfo = getResource(
                    ggrSessionHostInfoUrl,
                    new Pair<>("Authorization", "Basic " + new String(Base64.encodeBase64(auth.getBytes())))
            );
            if (sessionHostInfo == null || sessionHostInfo.isEmpty()) {
                throw new IllegalStateException("There is no host info for session: " + ggrSessionHostInfoUrl.getFile());
            }
            String selenoidHost = JsonPath.read(sessionHostInfo, "$.Name");
            return new URL("http", "docker.for.mac.localhost".equals(selenoidHost) ? "localhost" : selenoidHost, JsonPath.read(sessionHostInfo, "$.Port"), "");

        } catch (MalformedURLException mfe) {
            throw new IllegalStateException("Illegal generated selenoid url", mfe);
        }
    }
}
