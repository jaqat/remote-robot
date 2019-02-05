package io.github.jaqat.remoterobot.selenoid.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.jaqat.remoterobot.client.RemoteRobot;
import io.github.jaqat.remoterobot.selenoid.GgrUtils;
import io.github.jaqat.remoterobot.selenoid.SelenoidUtils;
import io.github.jaqat.remoterobot.selenoid.test.enums.Browser;
import io.github.jaqat.remoterobot.selenoid.test.enums.RemoteBrowserProvider;
import io.qameta.allure.Attachment;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.$;
import static io.github.jaqat.remoterobot.selenoid.test.config.TestsConfig.*;
import static io.github.jaqat.remoterobot.selenoid.test.enums.RemoteBrowserProvider.*;
import static java.awt.event.KeyEvent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoteRobotTest {

    private URL browserProviderUrl;

    public static Stream<Arguments> crossBrowserTests() {
        return Stream.of(Browser.values()).flatMap(
                browser -> Stream.of(
                        Arguments.of(GGR, browser),
                        Arguments.of(SELENOID, browser)
                )
        );
    }

    @ParameterizedTest(name = "[{0}:{1}] Check getting pixel color")
    @MethodSource("crossBrowserTests")
    @Feature("Get pixel color")
    void checkGetPixelColor(RemoteBrowserProvider remoteBrowserProvider, Browser browser) {
        browserProviderUrl = initRemoteWebDriver(browser, remoteBrowserProvider);
        openTestPage();
        RemoteRobot remoteRobot = getRemoteRobot(remoteBrowserProvider, browserProviderUrl, ((RemoteWebDriver) WebDriverRunner.getWebDriver()));

        assertEquals(Color.decode("#E9967A"), remoteRobot.getPixelColor(300, 500));
        assertEquals(Color.decode("#1E90FF"),remoteRobot.getPixelColor(600, 500));
        assertEquals(Color.decode("#B0C4DE"),remoteRobot.getPixelColor(730, 470));
    }

    @ParameterizedTest(name = "[{0}:{1}] Check click mouse")
    @MethodSource("crossBrowserTests")
    @Feature("Mouse click")
    void checkClickMouse(RemoteBrowserProvider remoteBrowserProvider, Browser browser) {
        browserProviderUrl = initRemoteWebDriver(browser, remoteBrowserProvider);
        openTestPage();
        RemoteRobot remoteRobot = getRemoteRobot(remoteBrowserProvider, browserProviderUrl, ((RemoteWebDriver) WebDriverRunner.getWebDriver()));
        remoteRobot.mouseClick(730, 490, InputEvent.BUTTON1_DOWN_MASK);
        $("h1").shouldHave(Condition.text("Page after click to button"));
    }

    @ParameterizedTest(name = "[{0}:{1}] Check capture screen ")
    @MethodSource("crossBrowserTests")
    @Feature("Capture screen")
    void checkCaptureScreen(RemoteBrowserProvider remoteBrowserProvider, Browser browser) throws IOException {
        browserProviderUrl = initRemoteWebDriver(browser, remoteBrowserProvider);
        openTestPage();

        RemoteRobot remoteRobot = getRemoteRobot(remoteBrowserProvider, browserProviderUrl, ((RemoteWebDriver) WebDriverRunner.getWebDriver()));

        File tempFile = File.createTempFile("checkCaptureString", "png");
        remoteRobot.captureScreen(tempFile.getAbsolutePath(), 100, 200, 1240, 700);
        BufferedImage actualImage = ImageIO.read(tempFile);

        BufferedImage expectedImage = null;
        switch (browser) {
            case CHROME:
                expectedImage = ImageIO.read(
                        new File(getClass().getClassLoader().getResource("tests/chrome/PartitialColoredPage.png").getFile())
                );
                break;

            case FIREFOX:
                expectedImage = ImageIO.read(
                        new File(getClass().getClassLoader().getResource("tests/firefox/PartitialColoredPage.png").getFile())
                );
                break;
            default:
                throw new IllegalStateException("Unsupported browse");
        }

        assertEquals(
                expectedImage.getWidth(),
                actualImage.getWidth(),
                "Unexpected captured image's width"
        );

        assertEquals(
                expectedImage.getHeight(),
                actualImage.getHeight(),
                "Unexpected captured image's height"
        );

        for (int x = 0; x < expectedImage.getWidth(); x++) {
            for (int y = 0; y < expectedImage.getHeight(); y++) {
                if (!(expectedImage.getRGB(x, y) == actualImage.getRGB(x, y))) {
                    throw new AssertionError("Images are not equals");
                }
            }
        }
    }

    @ParameterizedTest(name = "[{0}:{1}] Check key press")
    @MethodSource("crossBrowserTests")
    @Feature("Key press")
    void checkKeyPress(RemoteBrowserProvider remoteBrowserProvider, Browser browser) throws IOException {
        browserProviderUrl = initRemoteWebDriver(browser, remoteBrowserProvider);
        openTestPage();
        RemoteRobot remoteRobot = getRemoteRobot(remoteBrowserProvider, browserProviderUrl, ((RemoteWebDriver) WebDriverRunner.getWebDriver()));

        $("input").click();
        remoteRobot.pressKey(VK_T);
        remoteRobot.pressKey(VK_E);
        remoteRobot.pressKey(VK_S);
        remoteRobot.pressKey(VK_T);

        $("input").shouldHave(Condition.value("test"));
    }

    @Step("Open test page")
    private void openTestPage() {
        Selenide.open("http://test-site:80/test_page.html");
        $("div.centered").should(Condition.exist);
    }

    private RemoteRobot getRemoteRobot(RemoteBrowserProvider remoteBrowserProvider, URL remoteBrowserProviderUrl, RemoteWebDriver remoteWebDriver) {
        switch (remoteBrowserProvider) {
            case GGR:
                return GgrUtils.getRemoteRobot(remoteBrowserProviderUrl, remoteWebDriver);
            case SELENOID:
                return SelenoidUtils.getRemoteRobot(remoteBrowserProviderUrl, remoteWebDriver);
        }
        return null;
    }

    /*
     * Can be used for debug to show screenshots in Allure report
     */
    @Attachment(value = "{description}", type = "image/png")
    public static byte[] logImage(String description, byte[] imageBytes) {
        return imageBytes;
    }

    @Step("Init remote web driver")
    private URL initRemoteWebDriver(Browser browser, RemoteBrowserProvider remoteBrowserProvider) {
        URL browserProviderUrl = null;
        try {
            switch (remoteBrowserProvider) {
                case GGR:
                    browserProviderUrl = new URL("http://test:test@localhost:4000/wd/hub");
                    break;

                case SELENOID:
                    browserProviderUrl = new URL("http://localhost:4001/wd/hub");
                    break;
            }

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("enableVNC", true);
            capabilities.setCapability("screenResolution", SCREEN_WIDTH + "x" + SCREEN_HEIGHT + "x24");

            switch (browser) {
                case CHROME:
                    Configuration.browser = "chrome";
                    WebDriverRunner.setWebDriver(
                            new RemoteWebDriver(browserProviderUrl, new ChromeOptions().merge(capabilities))
                    );
                    break;

                case FIREFOX:
                    Configuration.browser = "firefox";
                    WebDriverRunner.setWebDriver(
                            new RemoteWebDriver(browserProviderUrl, new FirefoxOptions().merge(capabilities))
                    );
                    break;
            }
            WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            return browserProviderUrl;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid url" + e.getMessage(), e);
        }
    }
}
