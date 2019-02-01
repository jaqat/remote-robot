package com.github.jaqat.remoterobot.commons.utils;

import com.github.jaqat.remoterobot.commons.protocol.Response;

import java.awt.event.KeyEvent;

import static com.github.jaqat.remoterobot.commons.protocol.Operation.*;

public class KeyUtil extends JavaRobotUtil {

    public Response keyPress(int keyCode) {
        return executeOperation(
                KEY_PRESS,
                () -> {
                    robot.keyPress(keyCode);
                    robot.keyRelease(keyCode);
                    return null;
                },
                (result) -> "Pressed " + KeyEvent.getKeyText(keyCode),
                "Unable to press " + KeyEvent.getKeyText(keyCode) + " key."
        );
    }
}
