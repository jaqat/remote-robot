package com.github.jaqat.remoterobot.client;

import com.github.jaqat.remoterobot.commons.utils.Base64;
import com.github.jaqat.remoterobot.commons.protocol.Request;
import com.github.jaqat.remoterobot.commons.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static com.github.jaqat.remoterobot.commons.protocol.Operation.*;

public class RemoteRobot {

    private static Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private int port;
    private String host;

    public RemoteRobot(URL url) {
        this.host = url.getHost();
        this.port = url.getPort();
    }

    /**
     * Press (and release) keyboard key on remote server
     *
     * @param keyCode {@link java.awt.event.KeyEvent}
     * @return success flag
     */
    public boolean pressKey(int keyCode) {
        return sendCommandMessage(
                new Request(KEY_PRESS, keyCode)
        ).isSuccess();
    }

    /**
     * Get color of pixel at remote server's screen
     *
     * @param x - horizontal coordinate (pixels)
     * @param y - vertical coordinate (pixels)
     * @return {@link Color}
     */
    public Color getPixelColor(int x, int y) {
        Response response = sendCommandMessage(
                new Request(GET_PIXEL_COLOR, x, y)
        );
        return response.getResultObject() == null ? null : (Color) response.getResultObject();
    }

    /**
     * Capture remote screen
     *
     * @return byte[]  - bytes of PNG image
     */
    public byte[] captureScreen() {
        try {
            Response response = sendCommandMessage(new Request(CAPTURE_SCREEN));
            if (response.isSuccess() && response.getResultObject() != null) {
                Base64 base64 = new Base64();
                return Base64.decode(response.getResultObject().toString());
            }
        } catch (Exception e) {
            LOGGER.error("Error while capturing remote screen : " + e.getMessage());
        }
        return null;
    }

    /**
     * Capture remote screen with saving to required file
     *
     * @param fileNameSaveTo - absolute path of target file with screenshot
     * @return success flag
     */
    public boolean captureScreen(String fileNameSaveTo) {
        try {
            Response response = sendCommandMessage(new Request(CAPTURE_SCREEN));
            if (response.isSuccess() && response.getResultObject() != null) {
                saveByteArrayToFile(fileNameSaveTo, Base64.decode(response.getResultObject().toString()));
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Error while saving captured remote screen to local file : " + e.getMessage());
        }
        return false;
    }

    /**
     * Capture remote screen partially
     *
     * @return byte[]  - bytes of PNG image
     */
    public byte[] captureScreen(int x0, int y0, int x1, int y1) {
        try {
            Response response = sendCommandMessage(new Request(CAPTURE_SCREEN, x0, y0, x1, y1));
            if (response.isSuccess() && response.getResultObject() != null) {
                Base64 base64 = new Base64();
                return Base64.decode(response.getResultObject().toString());
            }
        } catch (Exception e) {
            LOGGER.error("Error while capturing remote screen : " + e.getMessage());
        }
        return null;
    }

    /**
     * Capture remote screen partially with saving to required file
     *
     * @param fileNameSaveTo
     * @return success flag
     */
    /**
     * Partially capture remote screen with saving to required file
     * @param fileNameSaveTo - absolute path of target file with screenshot
     * @param x - horizontal coordinate (pixels)
     * @param y - vertical coordinate (pixels)
     * @param width (pixels)
     * @param height (pixels)
     * @return success flag
     */
    public boolean captureScreen(String fileNameSaveTo, int x, int y, int width, int height) {
        try {
            Response response = sendCommandMessage(new Request(CAPTURE_SCREEN, x, y, width, height));
            if (response.isSuccess() && response.getResultObject() != null) {
                saveByteArrayToFile(fileNameSaveTo, Base64.decode(response.getResultObject().toString()));
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Error while capturing remote screen : " + e.getMessage());
        }
        return false;
    }

    private void saveByteArrayToFile(String fileNameSaveTo, byte[] imageBytes) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(fileNameSaveTo));
            fileOutputStream.write(imageBytes);
            fileOutputStream.close();
            fileOutputStream.flush();
        } catch (Exception e) {
            LOGGER.error("Error while saving remote screen to local file : " + e.getMessage());
        }
    }

    /**
     * Mouse click at remote server
     *
     * @param x - horizontal coordinate
     * @param y - vertica coordinate
     * @param buttonMask {@link java.awt.event.InputEvent} f.e. InputEvent.BUTTON1_MASK
     * @return flag of success
     */
    public boolean mouseClick(int x, int y, int buttonMask) {
        return sendCommandMessage(
                new Request(MOUSE_CLICK, x, y, buttonMask)
        ).isSuccess();
    }

    private Response sendCommandMessage(Request request) {
        return new Client().sendMessage(request, this.host, this.port);
    }

    public static void main(String[] args) {
        System.out.println("test");
    }
}
