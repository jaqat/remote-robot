package io.github.jaqat.remoterobot.server.utils;

import io.github.jaqat.remoterobot.protocol.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static io.github.jaqat.remoterobot.protocol.Operation.*;

public class ScreenUtil extends JavaRobotUtil {

    public Response getColor(int x, int y) {
        return executeOperation(
                GET_PIXEL_COLOR,
                () -> robot.getPixelColor(x, y),
                (color) -> String.format("Color of pixel[%d x %d]: %s", x, y, color.toString()),
                "Unable to get pixel color"
        );
    }

    public Response captureScreen() {
        return executeOperation(
                CAPTURE_SCREEN,
                () -> {
                    Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage bufferedImage = this.robot.createScreenCapture(rectangle);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", Base64.getEncoder().wrap(byteArrayOutputStream));
                    return byteArrayOutputStream.toString("UTF-8");
                },
                (screen) -> "Captured Remote Screen Successfully",
                "Unable to capture screen"
        );
    }

    public Response captureScreen(int x, int y, int width, int height) {
        return executeOperation(
                CAPTURE_SCREEN,
                () -> {
                    Rectangle rectangle = new Rectangle(x, y, width, height);
                    BufferedImage bufferedImage = this.robot.createScreenCapture(rectangle);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", Base64.getEncoder().wrap(byteArrayOutputStream));
                    return byteArrayOutputStream.toString("UTF-8");
                },
                (screen) -> "Captured Remote Screen Successfully",
                "Unable to capture screen"
        );
    }
}
