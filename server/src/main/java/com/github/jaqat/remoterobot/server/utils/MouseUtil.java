package com.github.jaqat.remoterobot.server.utils;

import com.github.jaqat.remoterobot.commons.protocol.Response;

import static com.github.jaqat.remoterobot.commons.protocol.Operation.MOUSE_CLICK;

public class MouseUtil extends JavaRobotUtil {

    public Response mouseClick(int x, int y, int buttonMask) {
        return executeOperation(
                MOUSE_CLICK,
                () -> {
                    robot.mouseMove(x, y);
                    robot.mousePress(buttonMask);
                    robot.mouseRelease(buttonMask);
                    return null;
                },
                (result) -> String.format("Clicked to coordinates [%d : %d]", x, y),
                "Unable to mouse click."
        );
    }
}
