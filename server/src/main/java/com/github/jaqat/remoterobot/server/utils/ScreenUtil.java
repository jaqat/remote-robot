package com.github.jaqat.remoterobot.server.utils;

import com.github.jaqat.remoterobot.commons.protocol.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static com.github.jaqat.remoterobot.commons.protocol.Operation.*;

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
//                    Base64
//                    Base64.OutputStream outputStream = new Base64.OutputStream(byteArrayOutputStream);
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
//                    Base64.OutputStream outputStream = new Base64.OutputStream(byteArrayOutputStream);
//                    ImageIO.write(bufferedImage, "png", outputStream);
                    ImageIO.write(bufferedImage, "png", Base64.getEncoder().wrap(byteArrayOutputStream));
                    return byteArrayOutputStream.toString("UTF-8");
                },
                (screen) -> "Captured Remote Screen Successfully",
                "Unable to capture screen"
        );
    }
}
