package com.github.jaqat.remoterobot.client;

import com.github.jaqat.remoterobot.commons.protocol.Request;
import com.github.jaqat.remoterobot.commons.protocol.Response;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Base64;

import static com.github.jaqat.remoterobot.commons.protocol.Operation.*;

public class RemoteRobot {

    private static Logger LOGGER = LoggerFactory.getLogger("RemoteRobot");

    private static int MAX_TRANSFER_SIZE = 10485760;

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
        return sendRequest(
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
        Response response = sendRequest(
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
            Response response = sendRequest(new Request(CAPTURE_SCREEN));
            if (response.isSuccess() && response.getResultObject() != null) {
                return Base64.getDecoder().decode(response.getResultObject().toString());
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
        Response response = sendRequest(new Request(CAPTURE_SCREEN));
        if (response.isSuccess() && response.getResultObject() != null) {
            saveByteArrayToFile(fileNameSaveTo, Base64.getDecoder().decode(response.getResultObject().toString()));
            return true;
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
            Response response = sendRequest(new Request(CAPTURE_SCREEN, x0, y0, x1, y1));
            if (response.isSuccess() && response.getResultObject() != null) {
                return Base64.getDecoder().decode(response.getResultObject().toString());
            }
        } catch (Exception e) {
            LOGGER.error("Error while capturing remote screen : " + e.getMessage());
        }
        return null;
    }

    /**
     * Partially capture remote screen with saving to required file
     *
     * @param fileNameSaveTo - absolute path of target file with screenshot
     * @param x              - horizontal coordinate (pixels)
     * @param y              - vertical coordinate (pixels)
     * @param width          (pixels)
     * @param height         (pixels)
     * @return success flag
     */

    public boolean captureScreen(String fileNameSaveTo, int x, int y, int width, int height) {
        try {
            Response response = sendRequest(new Request(CAPTURE_SCREEN, x, y, width, height));
            if (response.isSuccess() && response.getResultObject() != null) {
                saveByteArrayToFile(fileNameSaveTo, Base64.getDecoder().decode(response.getResultObject().toString()));
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
     * @param x          - horizontal coordinate
     * @param y          - vertica coordinate
     * @param buttonMask {@link java.awt.event.InputEvent} f.e. InputEvent.BUTTON1_MASK
     * @return flag of success
     */
    public boolean mouseClick(int x, int y, int buttonMask) {
        return sendRequest(
                new Request(MOUSE_CLICK, x, y, buttonMask)
        ).isSuccess();
    }

    private Response sendRequest(Request request) {
        Response response;

        try {
            NioSocketConnector socketConnector = new NioSocketConnector();
            ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();
            factory.setDecoderMaxObjectSize(MAX_TRANSFER_SIZE);
            factory.setEncoderMaxObjectSize(MAX_TRANSFER_SIZE);
            socketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
            socketConnector.setHandler(new com.github.jaqat.remoterobot.client.ClientHandler(request));
            ConnectFuture connectFuture = socketConnector.connect(new InetSocketAddress(host, port));
            connectFuture.awaitUninterruptibly();
            if (connectFuture.isConnected()) {
                IoSession session = connectFuture.getSession();
                session.getConfig().setUseReadOperation(true);
                session.getCloseFuture().awaitUninterruptibly();
                response = ((ClientHandler) session.getHandler()).getResponse();
                if (response == null){
                    response = new Response(request.getOperation())
                            .withSuccess(false)
                            .withMessage("Bad response from server");
                }
                session.close(true);
            } else {
                response = new Response(request.getOperation())
                        .withSuccess(false)
                        .withMessage(String.format("Server [%s:%s] is not reachable... ", host, port));
            }
            socketConnector.dispose();
        } catch (Exception e) {
            response = new Response(request.getOperation())
                    .withSuccess(false)
                    .withMessage("Error in interaction with Remote Robot Server: " + e.getMessage() );
        }

        // Log result of interaction
        if (response.isSuccess()) {
            LOGGER.info(response.getMessage());
        } else {
            LOGGER.error(response.getMessage());
        }
        return response;
    }
}
