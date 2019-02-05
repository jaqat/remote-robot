package io.github.jaqat.remoterobot.server.utils;

import io.github.jaqat.remoterobot.common.Response;
import java.awt.event.KeyEvent;

import static io.github.jaqat.remoterobot.common.Operation.KEY_PRESS;

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
